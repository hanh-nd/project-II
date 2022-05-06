package me.hanhngo.qrcode.view.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import me.hanhngo.qrcode.databinding.FragmentCameraBinding
import me.hanhngo.qrcode.domain.schema.Email
import me.hanhngo.qrcode.domain.schema.Other
import me.hanhngo.qrcode.domain.schema.Url
import me.hanhngo.qrcode.util.parseBarcode
import me.hanhngo.qrcode.util.parseFormat
import me.hanhngo.qrcode.util.parseSchema
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
@androidx.camera.core.ExperimentalGetImage
class CameraFragment : Fragment() {
    private var _binding: FragmentCameraBinding? = null
    private val binding: FragmentCameraBinding get() = _binding!!

    private var previewView: PreviewView? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector: CameraSelector? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(options)

    private lateinit var cameraExecutor: ExecutorService


    private val viewModel: CameraViewModel by viewModels()
    private val isProcessingBarcode: AtomicBoolean = AtomicBoolean(false)

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }

            if (granted) {
                startCamera()
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data!!
                processImageUri(barcodeScanner, uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCameraBinding.inflate(layoutInflater)
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        previewView = binding.previewView
        isProcessingBarcode.set(false)
        binding.imageChooseBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProvider = cameraProviderFuture.get()
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            try {
                // Bind use cases to camera
                bindPreviewUseCase()
                bindAnalyseUseCase()
            } catch (exc: Exception) {
                println("Use case binding failed: $exc")
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindPreviewUseCase() {
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }
        previewUseCase = Preview.Builder()
            .setTargetRotation(previewView!!.display.rotation)
            .build()
        //Attach the PreviewView surface provider to the preview use case.
        previewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)
        try {
            cameraProvider!!.bindToLifecycle(
                this,
                cameraSelector!!,
                previewUseCase
            )

        } catch (illegalStateException: IllegalStateException) {
            println(illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            println(illegalArgumentException.message ?: "IllegalArgumentException")
        }
    }

    private fun bindAnalyseUseCase() {
        // Note that if you know which format of barcode your app is dealing with,
        // detection will be faster
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }
        analysisUseCase = ImageAnalysis.Builder()
            .setTargetRotation(previewView!!.display.rotation)
            .setImageQueueDepth(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()
        analysisUseCase?.setAnalyzer(cameraExecutor) { imageProxy ->
            processImageProxy(barcodeScanner, imageProxy)
        }
        try {
            cameraProvider!!.bindToLifecycle(
                this,
                cameraSelector!!,
                analysisUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            println(illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            println(illegalArgumentException.message ?: "IllegalArgumentException")
        }
    }

    private fun processImageUri(
        barcodeScanner: BarcodeScanner,
        uri: Uri
    ) {
        try {
            val inputImage = InputImage.fromFilePath(requireContext(), uri)
            processInputImage(barcodeScanner, inputImage, isInputFromGallery = true)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        val inputImage =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        processInputImage(barcodeScanner, inputImage) {
            imageProxy.close()
        }
    }

    private fun processInputImage(
        barcodeScanner: BarcodeScanner,
        inputImage: InputImage,
        isInputFromGallery: Boolean = false,
        onCompleted: () -> Unit = {}
    ) {
        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                val barcode = barcodes.getOrNull(0)
                if (barcode != null && isProcessingBarcode.compareAndSet(false, true)) {
                    doOnBarcodeScanned(barcode)
                } else if (isInputFromGallery && barcode == null) {
                    Toast.makeText(
                        requireContext(),
                        "Unable to scan code! Please rescan or choose another image.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {
                println(it.message ?: it.toString())
                Toast.makeText(
                    requireContext(),
                    "Unable to scan code! Please rescan or choose another image.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnCompleteListener {
                onCompleted()
            }
    }

    private fun doOnBarcodeScanned(barcode: Barcode) {
        val barcodeEntity = parseBarcode(barcode)
        val schema = parseSchema(barcode.rawValue.toString())
        val format = parseFormat(barcode)

        viewModel.insert(barcodeEntity)

        when (schema) {
            is Email -> NavHostFragment.findNavController(this).navigate(
                CameraFragmentDirections.actionCameraFragmentToEmailFragment(schema, format)
            )

            is Url -> NavHostFragment.findNavController(this).navigate(
                CameraFragmentDirections.actionCameraFragmentToUrlFragment(schema, format)
            )
            else -> NavHostFragment.findNavController(this).navigate(
                CameraFragmentDirections.actionCameraFragmentToOtherFragment(
                    schema as Other,
                    format
                )
            )
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    startCamera()
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraExecutor.shutdown()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}
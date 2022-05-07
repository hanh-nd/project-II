package me.hanhngo.qrcode.view.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
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
import com.google.mlkit.vision.common.internal.ImageConvertUtils
import dagger.hilt.android.AndroidEntryPoint
import me.hanhngo.qrcode.R
import me.hanhngo.qrcode.databinding.FragmentCameraBinding
import me.hanhngo.qrcode.domain.schema.Email
import me.hanhngo.qrcode.domain.schema.Other
import me.hanhngo.qrcode.domain.schema.Url
import me.hanhngo.qrcode.util.extension.toInputImage
import me.hanhngo.qrcode.util.parseBarcode
import me.hanhngo.qrcode.util.parseFormat
import me.hanhngo.qrcode.util.parseSchema
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

@AndroidEntryPoint
@androidx.camera.core.ExperimentalGetImage
class CameraFragment : Fragment(), SurfaceHolder.Callback {
    private var _binding: FragmentCameraBinding? = null
    private val binding: FragmentCameraBinding get() = _binding!!

    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector: CameraSelector? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null

    private val lensFacing = CameraSelector.LENS_FACING_BACK
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(options)

    private lateinit var cameraExecutor: ExecutorService

    private var previewView: PreviewView? = null
    private lateinit var holder: SurfaceHolder

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

        binding.overlay.apply {
            setZOrderOnTop(true)
        }

        binding.imageChooseBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        holder = binding.overlay.holder.apply {
            setFormat(PixelFormat.TRANSLUCENT)
            addCallback(this@CameraFragment)
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
            if (imageProxy.image == null) {
                return@setAnalyzer
            }

            val inputImage = InputImage.fromMediaImage(
                imageProxy.image!!,
                imageProxy.imageInfo.rotationDegrees
            )
            val bitmap = ImageConvertUtils.getInstance().getUpRightBitmap(inputImage)

            val width = bitmap.width
            val height = bitmap.height

            var diameter = min(width, height)
            val offset = (CAMERA_PREVIEW_SIZE * diameter).toInt()
            diameter -= offset

            val left = (width / 2 - diameter / 3)
            val top = (height / 2 - diameter / 3)
            val right = (width / 2 + diameter / 3)
            val bottom = (height / 2 + diameter / 3)

            val boxHeight = bottom - top
            val boxWidth = right - left

            val inputBitmap =
                Bitmap.createBitmap(bitmap, left, top, boxWidth, boxHeight).toInputImage(imageProxy)

            processInputImage(barcodeScanner, inputBitmap) {
                imageProxy.close()
            }
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

    private fun drawFocusRect() {
        val height = previewView?.height ?: 0
        val width = previewView?.width ?: 0

        var diameter = min(width, height)
        val offset = (CAMERA_PREVIEW_SIZE * diameter).toInt()
        diameter -= offset

        val canvas = holder.lockCanvas()

        val paint = Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(requireContext(), R.color.white)
            strokeWidth = 5F
        }


        val left = (width / 2 - diameter / 3)
        val top = (height / 2 - diameter / 3)
        val right = (width / 2 + diameter / 3)
        val bottom = (height / 2 + diameter / 3)

        canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        holder.unlockCanvasAndPost(canvas)
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
        private const val CAMERA_PREVIEW_SIZE = 0.05 // percentage
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        drawFocusRect()
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
    }
}
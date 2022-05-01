package me.hanhngo.qrcode

import android.app.Application
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.ExecutionException
import javax.inject.Inject


@HiltViewModel
class CameraXViewModel @Inject constructor(private val application: Application) :
    ViewModel() {

    private var _cameraProvider: MutableLiveData<ProcessCameraProvider>? = null

    val cameraProvider: LiveData<ProcessCameraProvider>
        get() {
            if (_cameraProvider == null) {
                _cameraProvider = MutableLiveData()
                val cameraProviderFuture = ProcessCameraProvider.getInstance(application)
                cameraProviderFuture.addListener(
                    {
                        try {
                            _cameraProvider!!.setValue(cameraProviderFuture.get())
                        } catch (e: ExecutionException) {
                            println("Unhandled exception")
                        }
                    },
                    ContextCompat.getMainExecutor(application)
                )
            }
            return _cameraProvider!!
        }
}
package ir.amirreza.module6_gholami.ui.features.qr_code

import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import ir.amirreza.module6_gholami.data.states.LocaleAppState
import ir.amirreza.module6_gholami.utils.AppPages
import ir.amirreza.module6_gholami.utils.QrCodeAnalyzer

@ExperimentalGetImage
@Composable
fun QrCodeScannerScreen() {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current
    val previewView = remember {
        PreviewView(context)
    }
    val appState = LocaleAppState.current
    DisposableEffect(key1 = Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context)
        cameraProvider.addListener(
            {
                var imageAnalyzed= false
                val cameraFuture = cameraProvider.get()
                val imageAnalysis = ImageAnalysis.Builder()
                    .setImageQueueDepth(1)
                    .build().apply {
                        setAnalyzer(
                            ContextCompat.getMainExecutor(context),
                            QrCodeAnalyzer(
                                previewView
                            ) {
                                if (imageAnalyzed.not()){
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT)
                                        .show()
                                    appState.navigation.navigate(
                                        AppPages.ReceiveFile.route + "?key=" + it
                                    )
                                    imageAnalyzed = true
                                }
                            }
                        )
                    }
                val preview = Preview.Builder().build()
                    .apply { setSurfaceProvider(previewView.surfaceProvider) }
                try {
                    cameraFuture.unbindAll()
                    cameraFuture.bindToLifecycle(
                        lifecycle,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            ContextCompat.getMainExecutor(context),
        )
        onDispose { }
    }
    AndroidView(factory = {
        previewView
    })
}
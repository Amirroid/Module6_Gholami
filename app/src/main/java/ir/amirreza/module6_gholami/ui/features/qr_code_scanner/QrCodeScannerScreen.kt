package ir.amirreza.module6_gholami.ui.features.qr_code_scanner

import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import ir.amirreza.module6_gholami.data.states.LocaleAppState
import ir.amirreza.module6_gholami.utils.AppPages

@ExperimentalGetImage
@Composable
fun QrCodeScannerScreen() {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current
    val option = remember {
        BarcodeScannerOptions.Builder()
            .enableAllPotentialBarcodes()
            .build()
    }
    val client = remember { BarcodeScanning.getClient(option) }
    val previewView = remember {
        PreviewView(context)
    }
    val appState = LocaleAppState.current
    DisposableEffect(key1 = Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context)
        cameraProvider.addListener(
            {
                val cameraFuture = cameraProvider.get()
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(previewView.width, previewView.height))
                    .build().apply {
                        setAnalyzer(
                            ContextCompat.getMainExecutor(context)
                        ) {
                            Log.d("sdfsds", "QrCodeScannerScreen:${it.imageInfo.rotationDegrees} ")
                            runCatching {
                                if (it.image != null) {
                                    val inputMedia =
                                        InputImage.fromMediaImage(
                                            it.image!!,
                                            it.imageInfo.rotationDegrees
                                        )
                                    client.process(inputMedia).addOnSuccessListener { barcodes ->
                                        barcodes.firstOrNull { barcode ->  barcode.rawValue != null }
                                            ?.let { barcode ->
                                                Toast.makeText(context, "", Toast.LENGTH_SHORT)
                                                    .show()
                                                appState.navigation.navigate(
                                                    AppPages.ReceiveFile.route + "?key=" + barcode.rawValue!!
                                                )
                                            }
                                    }.addOnFailureListener { fail ->
                                        Log.e(
                                            "dfsgjfiuehguid",
                                            "QrCodeScannerScreen: ${fail.message}"
                                        )
                                    }
                                }
                            }
                            it.close()
                        }
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
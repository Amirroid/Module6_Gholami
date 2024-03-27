package ir.amirreza.module6_gholami.utils

import android.util.Log
import android.widget.Toast
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis.Analyzer
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.compose.runtime.remember
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@ExperimentalGetImage
class QrCodeAnalyzer(
    private val previewView: PreviewView,
    private val onScan: (String) -> Unit
) : Analyzer {
    private val option = BarcodeScannerOptions.Builder()
        .enableAllPotentialBarcodes()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    private val client = BarcodeScanning.getClient(option)
    override fun analyze(image: ImageProxy) {
        Log.d("sdfsds", "QrCodeScannerScreen:${image.imageInfo.rotationDegrees} ")
        runCatching {
            val bitmap = previewView.bitmap
            if (bitmap != null) {
                val inputMedia =
                    InputImage.fromBitmap(
                        bitmap,
                        image.imageInfo.rotationDegrees
                    )
                client.process(inputMedia).addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        barcodes.maxByOrNull { barcode ->
                            (barcode.rawValue ?: "").length
                        }?.let { barcode ->
                            if (barcode.rawValue.isNullOrEmpty().not()){
                                barcode.rawValue?.let(onScan)
                            }
                        }
                    }
                    image.close()
                }.addOnFailureListener { fail ->
                    Log.e(
                        "dfsgjfiuehguid",
                        "QrCodeScannerScreen: ${fail.message}"
                    )
                }
            }
        }
    }
}
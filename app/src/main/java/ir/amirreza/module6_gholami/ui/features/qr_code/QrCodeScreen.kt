package ir.amirreza.module6_gholami.ui.features.qr_code

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import ir.amirreza.module6_gholami.data.states.LocaleAppState
import ir.amirreza.module6_gholami.utils.AppPages

@Composable
fun QrCodeScreen(key: String, filename: String) {
    var bitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    val appState = LocaleAppState.current
    LaunchedEffect(key1 = Unit) {
        val barcodeEncoder = BarcodeEncoder()
        runCatching {
            bitmap = barcodeEncoder.encodeBitmap(
                key,
                BarcodeFormat.QR_CODE,
                1000,
                1000,
            ).asImageBitmap()
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!,
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )
        }
        Text(text = "Scan to receive", modifier = Modifier.padding(top = 12.dp))
        Button(onClick = {
            appState.navigation.navigate(
                AppPages.Devices.route + "?filename=" + filename
            )
        }) {
            Text(text = "Go to send")
        }
    }
}
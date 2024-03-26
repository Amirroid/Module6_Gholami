package ir.amirreza.module6_gholami.ui.features.receive_file

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import ir.amirreza.module6_gholami.data.helpers.BluetoothHelper
import ir.amirreza.module6_gholami.data.states.ConnectiveStatus
import ir.amirreza.module6_gholami.data.states.LocaleAppState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("MissingPermission")
@Composable
fun ReceiveFileScreen() {
    val context = LocalContext.current
    val helper = remember {
        BluetoothHelper(context)
    }
    val appState = LocaleAppState.current
    var connecting by remember {
        mutableStateOf(true)
    }
    DisposableEffect(key1 = Unit) {
        appState.scope.launch(Dispatchers.IO) {
            helper.startSocket().collectLatest {
                if (it is ConnectiveStatus.Success) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Connected successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    connecting = false
                }
            }
        }
        onDispose {
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (connecting) {
            CircularProgressIndicator()
        } else {
            LinearProgressIndicator(progress = 1f, modifier = Modifier.fillMaxWidth(0.6f))
        }
    }
}
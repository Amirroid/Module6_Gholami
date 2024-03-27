package ir.amirreza.module6_gholami.ui.features.send

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
<<<<<<< Updated upstream
=======
import androidx.compose.runtime.mutableFloatStateOf
>>>>>>> Stashed changes
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
<<<<<<< Updated upstream

@SuppressLint("MissingPermission")
@Composable
fun SendFileScreen(address: String) {
=======
import java.io.File

@SuppressLint("MissingPermission")
@Composable
fun SendFileScreen(address: String, filename: String) {
>>>>>>> Stashed changes
    val context = LocalContext.current
    val helper = remember {
        BluetoothHelper(context)
    }
    val appState = LocaleAppState.current
    val scope = appState.scope
    var connecting by remember {
        mutableStateOf(true)
    }
<<<<<<< Updated upstream
=======
    var progress by remember {
        mutableFloatStateOf(0f)
    }
>>>>>>> Stashed changes
    DisposableEffect(key1 = Unit) {
        helper.getAllDevices()
        scope.launch(Dispatchers.IO) {
            helper.devices.first()
            helper.scannedDevices.first()
<<<<<<< Updated upstream
            helper.connectToAddress(address).launchIn(this)
            launch {
                helper.connected.collect {
                    if (it == null) return@collect
                    if (it.first) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Connected to ${it.second.name} successfully ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        connecting = false
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                            appState.navigation.popBackStack()
                        }
=======
            helper.connectToAddress(address).collect {
                if (it is ConnectiveStatus.Success) {
                    launch(Dispatchers.IO) {
                        helper.sendFile(File(context.cacheDir, filename))
                        progress = 1f
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Connected successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        connecting = false
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                        appState.navigation.popBackStack()
>>>>>>> Stashed changes
                    }
                }
            }
        }
        onDispose {
            helper.release()
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
<<<<<<< Updated upstream
            LinearProgressIndicator(progress = 1f, modifier = Modifier.fillMaxWidth(0.6f))
=======
            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth(0.6f))
>>>>>>> Stashed changes
        }
    }
}
package ir.amirreza.module6_gholami.ui.features.receive_file

import android.annotation.SuppressLint
<<<<<<< Updated upstream
import android.widget.Toast
=======
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
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
=======
import ir.amirreza.module6_gholami.data.helpers.CrypticHelper
import ir.amirreza.module6_gholami.data.states.LocaleAppState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@SuppressLint("MissingPermission")
@Composable
fun ReceiveFileScreen(key: String) {
>>>>>>> Stashed changes
    val context = LocalContext.current
    val helper = remember {
        BluetoothHelper(context)
    }
    val appState = LocaleAppState.current
    var connecting by remember {
        mutableStateOf(true)
    }
<<<<<<< Updated upstream
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
=======
    var progress by remember {
        mutableFloatStateOf(0f)
    }
    var isBluetoothEnabled by remember {
        mutableStateOf(helper.isEnabled)
    }
    val crypticHelper = remember {
        CrypticHelper()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun receive() {
        appState.scope.launch(Dispatchers.IO) {
            helper.startSocket().collect {
                launch(Dispatchers.Main) {
                    connecting = false
                    Toast.makeText(
                        context,
                        "Connected successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                launch(Dispatchers.IO) {
                    val file = File(context.cacheDir, "${System.currentTimeMillis()}.mp3")
                    file.createNewFile()
                    launch(Dispatchers.IO) {
                        helper.receiveFile(file)
                    }
                    delay(1000)
                    progress = 1f
                    crypticHelper.decryptFile(file, crypticHelper.getSecretKey(key))
                    withContext(Dispatchers.Main){
                        Toast.makeText(context, "play", Toast.LENGTH_SHORT).show()
                        val player =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                MediaPlayer(context)
                            } else {
                                MediaPlayer()
                            }
                        player.setDataSource(file.path)
                        player.prepare()
                        player.start()
                    }
                }
            }
        }
    }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            isBluetoothEnabled = it.resultCode == Activity.RESULT_OK
        }
    DisposableEffect(key1 = isBluetoothEnabled) {
        if (isBluetoothEnabled) {
            receive()
        } else {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            launcher.launch(intent)
        }
//            helper.connected.collectLatest {
//                if (it?.first == true) {
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(
//                            context,
//                            "Connected successfully",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        connecting = false
//                    }
//                    helper.receiveFile().collectLatest { p ->
//                        progress = p
//                    }
//                }
//            }
        onDispose {}
>>>>>>> Stashed changes
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
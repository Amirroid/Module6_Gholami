package ir.amirreza.module6_gholami.ui.features.devices

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.amirreza.module6_gholami.data.helpers.BluetoothHelper
import ir.amirreza.module6_gholami.data.states.LocaleAppState
import ir.amirreza.module6_gholami.utils.AppPages

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun DevicesScreen(filename: String) {
    val context = LocalContext.current
    val bluetoothHelper = remember {
        BluetoothHelper(context)
    }
    val appState = LocaleAppState.current
<<<<<<< Updated upstream
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                bluetoothHelper.getAllDevices()
            }
        }
    val devices by bluetoothHelper.devices.collectAsStateWithLifecycle(emptyList())
    val scannedDevices by bluetoothHelper.scannedDevices.collectAsStateWithLifecycle(emptyList())

    DisposableEffect(key1 = Unit) {
        if (bluetoothHelper.isEnabled.not()) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            launcher.launch(intent)
        } else {
=======
    val requestDiscoverableLancer =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
>>>>>>> Stashed changes
            bluetoothHelper.getAllDevices()
        }
    val devices by bluetoothHelper.devices.collectAsStateWithLifecycle(emptyList())
    val scannedDevices by bluetoothHelper.scannedDevices.collectAsStateWithLifecycle(emptyList())

    DisposableEffect(key1 = Unit) {
//        if (bluetoothHelper.isEnabled.not()) {
//            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            launcher.launch(intent)
//        } else {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3000)
        requestDiscoverableLancer.launch(intent)
//        }
        onDispose {
            bluetoothHelper.release()
        }
    }
    Column {
        CenterAlignedTopAppBar(title = { Text(text = "Devices") })
        LazyColumn {
            item("paired") {
                Text(
                    text = "Paired devices",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .padding(start = 8.dp)
                )
            }
            items(devices, key = { it.address }) { device ->
                ListItem(
                    headlineContent = { Text(text = device.name) },
                    supportingContent = {
                        Text(
                            text = device.address
                        )
                    }, modifier = Modifier.clickable {
                        appState.navigation.navigate(AppPages.SendFile.route + "?filename=$filename&address=${device.address}")
<<<<<<< Updated upstream
                    })
            }
            item("scanned") {
                Text(
                    text = "Scanned devices",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .padding(start = 8.dp)
                )
            }
            items(scannedDevices, key = { it.address }) { device ->
                ListItem(
                    headlineContent = { Text(text = device.name) },
                    supportingContent = {
                        Text(
                            text = device.address
                        )
=======
>>>>>>> Stashed changes
                    })
            }
            item("scanned") {
                Text(
                    text = "Scanned devices",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .padding(start = 8.dp)
                )
            }
            items(scannedDevices, key = { it.address }) { device ->
                if (device.name.isNullOrEmpty()) {
                    ListItem(
                        headlineContent = { Text(text = device.address ?: "") },
                        modifier = Modifier.clickable {
                            appState.navigation.navigate(AppPages.SendFile.route + "?filename=$filename&address=${device.address}")
                        })
                } else {
                    ListItem(
                        headlineContent = { Text(text = device.name ?: "") },
                        supportingContent = {
                            Text(
                                text = device.address ?: ""
                            )
                        }, modifier = Modifier.clickable {
                            appState.navigation.navigate(AppPages.SendFile.route + "?filename=$filename&address=${device.address}")
                        })
                }
            }
        }
    }
}
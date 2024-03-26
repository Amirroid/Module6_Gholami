package ir.amirreza.module6_gholami.ui.features.devices

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.amirreza.module6_gholami.data.helpers.BluetoothHelper

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun DevicesScreen() {
    val context = LocalContext.current
    val bluetoothHelper = remember {
        BluetoothHelper(context)
    }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                bluetoothHelper.getAllDevices()
            }
        }
    val devices by bluetoothHelper.devices.collectAsStateWithLifecycle(emptyList())
    DisposableEffect(key1 = Unit) {
        if (bluetoothHelper.isEnabled.not()) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            launcher.launch(intent)
        } else {
            bluetoothHelper.getAllDevices()
        }
        onDispose { bluetoothHelper.release() }
    }
    Column {
        CenterAlignedTopAppBar(title = { Text(text = "Devices") })
        LazyColumn {
            items(devices, key = { it.address }) { device ->
                ListItem(
                    headlineContent = { Text(text = device.name) },
                    supportingContent = {
                        Text(
                            text = device.address
                        )
                    })
            }
        }
    }
}
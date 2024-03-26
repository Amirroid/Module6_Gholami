package ir.amirreza.module6_gholami.data.receivers

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BluetoothStatusChangesReceiver(
    private val onReceiveDevice: (Boolean, BluetoothDevice) -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        device?.let {
            if (intent.action == BluetoothDevice.ACTION_ACL_CONNECTED) {
                onReceiveDevice.invoke(true, it)
            }
            if (intent.action == BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED) {
                onReceiveDevice.invoke(false, it)
            }
        }
    }
}
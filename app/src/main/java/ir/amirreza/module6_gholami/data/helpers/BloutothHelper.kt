package ir.amirreza.module6_gholami.data.helpers

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import ir.amirreza.module6_gholami.data.receivers.FoundDeviceListener
import ir.amirreza.module6_gholami.utils.checkPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class BluetoothHelper(private val context: Context) {
    private fun hasScanPermission() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.checkPermission(Manifest.permission.BLUETOOTH_SCAN)
    } else {
        true
    }

    fun hasPermission() = context.checkPermission(Manifest.permission.BLUETOOTH)


    private val _devices = MutableStateFlow<Set<BluetoothDevice>>(emptySet())
    val devices = _devices.asStateFlow().map { it.toList().distinct() }


    private val receiver = FoundDeviceListener { device ->
        _devices.update {
            if (device !in it) {
                it + device
            } else it
        }
    }


    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var adapter: BluetoothAdapter

    val isEnabled: Boolean get() = adapter.isEnabled

    init {
        initBluetooth()
    }


    @SuppressLint("MissingPermission")
    private fun initBluetooth() {
        bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        adapter = bluetoothManager.adapter
    }

    @SuppressLint("MissingPermission")
    fun getAllDevices() {
        if (hasScanPermission().not()) return
        adapter.startDiscovery()
        getAllBonded()
        scanDevices()
    }

    private fun scanDevices() {
        val intentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(receiver, intentFilter)
    }

    @SuppressLint("MissingPermission")
    private fun getAllBonded() {
        if (hasScanPermission().not()) return
        _devices.update { adapter.bondedDevices }
    }

    @SuppressLint("MissingPermission")
    fun release() {
        context.unregisterReceiver(receiver)
        if (hasScanPermission().not()) return
        adapter.cancelDiscovery()
    }
}
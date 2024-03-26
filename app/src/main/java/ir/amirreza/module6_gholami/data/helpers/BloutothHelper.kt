package ir.amirreza.module6_gholami.data.helpers

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import ir.amirreza.module6_gholami.data.receivers.BluetoothStatusChangesReceiver
import ir.amirreza.module6_gholami.data.receivers.FoundDeviceListener
import ir.amirreza.module6_gholami.data.states.ConnectiveStatus
import ir.amirreza.module6_gholami.utils.checkPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.util.UUID

class BluetoothHelper(private val context: Context) {
    private fun hasScanPermission() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.checkPermission(Manifest.permission.BLUETOOTH_SCAN)
    } else {
        true
    }

    fun hasPermission() = context.checkPermission(Manifest.permission.BLUETOOTH)
    private fun hasConnectPermission() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.checkPermission(Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        true
    }


    private val _devices = MutableStateFlow<Set<BluetoothDevice>>(emptySet())
    val devices = _devices.asStateFlow().map { it.toList().distinct() }


    private val _scannedDevices = MutableStateFlow<Set<BluetoothDevice>>(emptySet())
    val scannedDevices = _scannedDevices.asStateFlow().map { it.toList().distinct() }


    private val _connected = MutableStateFlow<Pair<Boolean, BluetoothDevice>?>(null)
    val connected = _connected.asStateFlow()


    private val receiver = FoundDeviceListener { device ->
        _scannedDevices.update {
            if (device !in it) {
                it + device
            } else it
        }
    }

    private val stateChangeReceiver = BluetoothStatusChangesReceiver { isConnected, device ->
        if (device in _scannedDevices.value || device in _devices.value) {
            _connected.update { Pair(isConnected, device) }
        } else _connected.update { null }
    }


    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var adapter: BluetoothAdapter

    private var mServerSocket: BluetoothServerSocket? = null
    private var mClientSocket: BluetoothSocket? = null

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
        receiveToConnects()
    }

    private fun receiveToConnects() {
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        }
        context.registerReceiver(stateChangeReceiver, filter)
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
        closeConnection()
    }


    @SuppressLint("MissingPermission")
    fun startSocket() = flow {
        if (hasConnectPermission()) {
            adapter.cancelDiscovery()
            mServerSocket = adapter.listenUsingRfcommWithServiceRecord(
                "record_socket",
                UUID.fromString(SERVICE_UUID)
            )
            var shouldLoop = true
            while (shouldLoop) {
                mClientSocket = try {
                    val socket = mServerSocket?.accept()
                    socket
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(ConnectiveStatus.Error(e.message ?: ""))
                    shouldLoop = false
                    null
                }
                mClientSocket?.let {
                    it.close()
                    emit(ConnectiveStatus.Success)
                }
            }
        }
    }.onCompletion {
        closeConnection()
    }.flowOn(Dispatchers.IO)

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) = flow {
        if (hasConnectPermission()) {
            adapter.cancelDiscovery()
            mClientSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_UUID))
            try {
                mClientSocket?.connect()
                emit(ConnectiveStatus.Success)
            } catch (e: Exception) {
                mClientSocket?.close()
                emit(ConnectiveStatus.Error(e.message ?: ""))
                e.printStackTrace()
            }
        }
    }.onCompletion { closeConnection() }.flowOn(Dispatchers.IO)

    private fun closeConnection() {
        mServerSocket?.close()
        mClientSocket?.close()
        mClientSocket = null
        mServerSocket = null
    }

    @SuppressLint("MissingPermission")
    fun connectToAddress(address: String): Flow<ConnectiveStatus> {
        val device =
            _devices.value.firstOrNull { it.address == address } ?: adapter.getRemoteDevice(address)
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, device.name.toString(), Toast.LENGTH_SHORT).show()
        }
        return connectToDevice(device)
    }

    companion object {
        const val SERVICE_UUID = "097a111d-81ce-4f9d-9d16-c94255d37ba4"
    }
}
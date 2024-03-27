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
<<<<<<< Updated upstream
=======
import android.util.Log
>>>>>>> Stashed changes
import android.widget.Toast
import ir.amirreza.module6_gholami.data.receivers.BluetoothStatusChangesReceiver
import ir.amirreza.module6_gholami.data.receivers.FoundDeviceListener
import ir.amirreza.module6_gholami.data.states.ConnectiveStatus
import ir.amirreza.module6_gholami.utils.checkPermission
import kotlinx.coroutines.Dispatchers
<<<<<<< Updated upstream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
=======
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
>>>>>>> Stashed changes
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
<<<<<<< Updated upstream
import kotlinx.coroutines.withContext
=======
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
>>>>>>> Stashed changes
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
    val devices = _devices.asStateFlow().map { it.toList().distinctBy { device -> device.address } }


    private val _scannedDevices = MutableStateFlow<Set<BluetoothDevice>>(emptySet())
    val scannedDevices =
        _scannedDevices.asStateFlow().map { it.toList().distinctBy { device -> device.address } }


    private val _connected = MutableStateFlow<Pair<Boolean, BluetoothDevice>?>(null)
    val connected = _connected.asStateFlow()


    private val _scannedDevices = MutableStateFlow<Set<BluetoothDevice>>(emptySet())
    val scannedDevices = _scannedDevices.asStateFlow().map { it.toList().distinct() }


    private val _connected = MutableStateFlow<Pair<Boolean, BluetoothDevice>?>(null)
    val connected = _connected.asStateFlow()


    private val receiver = FoundDeviceListener { device ->
        _scannedDevices.update {
<<<<<<< Updated upstream
            if (device !in it) {
=======
            if (device !in it && device !in _devices.value) {
>>>>>>> Stashed changes
                it + device
            } else it
        }
    }

<<<<<<< Updated upstream
    private val stateChangeReceiver = BluetoothStatusChangesReceiver { isConnected, device ->
        if (device in _scannedDevices.value || device in _devices.value) {
=======
    @SuppressLint("MissingPermission")
    private val stateChangeReceiver = BluetoothStatusChangesReceiver { isConnected, device ->
        if (hasScanPermission().not()) return@BluetoothStatusChangesReceiver
        if (adapter.bondedDevices.contains(device)) {
>>>>>>> Stashed changes
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
        val isStart = adapter.startDiscovery()
        Log.d("dsfsdfd", "getAllDevices: $isStart")
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
    fun release() = runCatching {
        context.unregisterReceiver(receiver)
        if (hasScanPermission().not()) return@runCatching
        adapter.cancelDiscovery()
        closeConnection()
    }


    @SuppressLint("MissingPermission")
    fun startSocket() = flow {
        if (hasConnectPermission()) {
            adapter.cancelDiscovery()
            mServerSocket = adapter.listenUsingRfcommWithServiceRecord(
<<<<<<< Updated upstream
                "record_socket",
                UUID.fromString(SERVICE_UUID)
=======
                "record_socket", UUID.fromString(SERVICE_UUID)
>>>>>>> Stashed changes
            )
            var shouldLoop = true
            while (shouldLoop) {
                mClientSocket = try {
<<<<<<< Updated upstream
                    val socket = mServerSocket?.accept()
                    socket
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(ConnectiveStatus.Error(e.message ?: ""))
=======
                    mServerSocket?.accept()
                } catch (e: Exception) {
                    e.printStackTrace()
>>>>>>> Stashed changes
                    shouldLoop = false
                    null
                }
                mClientSocket?.let {
<<<<<<< Updated upstream
                    it.close()
=======
//                    it.close()
>>>>>>> Stashed changes
                    emit(ConnectiveStatus.Success)
                }
            }
        }
    }.onCompletion {
        closeConnection()
<<<<<<< Updated upstream
    }.flowOn(Dispatchers.IO)
=======
    }.distinctUntilChanged().flowOn(Dispatchers.IO)
>>>>>>> Stashed changes

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
<<<<<<< Updated upstream
        mServerSocket?.close()
        mClientSocket?.close()
        mClientSocket = null
        mServerSocket = null
=======
//        mServerSocket?.close()
//        mClientSocket?.close()
//        mClientSocket = null
//        mServerSocket = null
>>>>>>> Stashed changes
    }

    @SuppressLint("MissingPermission")
    fun connectToAddress(address: String): Flow<ConnectiveStatus> {
        val device =
            _devices.value.firstOrNull { it.address == address } ?: adapter.getRemoteDevice(address)
        Handler(Looper.getMainLooper()).post {
<<<<<<< Updated upstream
            Toast.makeText(context, device.name.toString(), Toast.LENGTH_SHORT).show()
=======
            Toast.makeText(context, device.name ?: "", Toast.LENGTH_SHORT).show()
>>>>>>> Stashed changes
        }
        return connectToDevice(device)
    }

<<<<<<< Updated upstream
=======
    fun receiveFile(file: File) {
        mClientSocket?.let { socket ->
            if (!socket.isConnected) {
                Log.d("dsfdsfs", "receiveFile: canceled")
                return
            }
            while (true) {
                val fos = FileOutputStream(file)
                val inputStream = socket.inputStream
                try {
//                    val buffer = ByteArray(1024)
//                    var bytesRead: Int
//                    Log.d("sdfdsf", "receiveFile: ${inputStream.available()}")
//                    var count = 0
//                    while (inputStream.read(buffer).also {
//                            bytesRead = it
//                            count += bytesRead
//                        } != -1) {
//                        fos.write(buffer, 0, bytesRead)
//                    }
                    inputStream.copyTo(fos)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }


    fun sendFile(file: File) {
        mClientSocket?.let { socket ->
            if (!socket.isConnected) {
                return
            }

            val fis = FileInputStream(file)
            val os = socket.outputStream
            fis.copyTo(os)
//            try {
//                val buffer = ByteArray(1024)
//                var bytesRead: Int
//                while (fis.read(buffer).also { bytesRead = it } != -1) {
//                    os.write(buffer, 0, bytesRead)
//                }
//                socket
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
        }
    }

>>>>>>> Stashed changes
    companion object {
        const val SERVICE_UUID = "097a111d-81ce-4f9d-9d16-c94255d37ba4"
    }
}
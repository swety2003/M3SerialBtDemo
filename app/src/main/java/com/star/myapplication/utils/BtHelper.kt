package com.star.myapplication.utils

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED
import androidx.core.content.ContextCompat.registerReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*


enum class ConnectState {
    NotConnect,
    Connecting,
    AlreadyConnected
}



class BtHelper {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var keepReceive: Boolean = true

    companion object {
        private const val TAG = "BtHelper"

        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            BtHelper()
        }
    }

    fun init(bluetoothAdapter: BluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter
    }

    var inited = false

    private var mReceive: BluetoothStateBroadcastReceive? = null


    private fun registerBluetoothReceiver(context: Context) {
        if (mReceive == null) {
            mReceive = BluetoothStateBroadcastReceive()
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_OFF")
        intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_ON")
        registerReceiver(context,mReceive, intentFilter,RECEIVER_NOT_EXPORTED)
    }
    fun init(context: Context): Boolean {
        if (inited) return inited
        val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
        this.bluetoothAdapter = bluetoothManager.adapter
        inited= bluetoothAdapter!=null

//        registerBluetoothReceiver(context)
        return inited
    }

    fun checkBluetooth(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                && bluetoothAdapter?.isEnabled == true
    }

    @SuppressLint("MissingPermission")
    fun queryPairDevices(): Set<BluetoothDevice>? {
        if (bluetoothAdapter == null) {
            Log.e(TAG, "queryPairDevices: bluetoothAdapter is null!")
            return null
        }

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter!!.bondedDevices

        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address

            Log.i(TAG, "queryPairDevices: deveice name=$deviceName, mac=$deviceHardwareAddress")
        }

        return pairedDevices
    }

    @SuppressLint("MissingPermission")
    suspend fun connectDevice(device: BluetoothDevice, onConnected : (socket: Result<BluetoothSocket>) -> Unit) {
        val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"))
        }

        withContext(Dispatchers.IO) {

            kotlin.runCatching {
                // 开始连接前应该关闭扫描，否则会减慢连接速度
                bluetoothAdapter?.cancelDiscovery()

                mmSocket?.connect()
            }.fold({
                withContext(Dispatchers.Main) {
                    onConnected(Result.success(mmSocket!!))
                }
            }, {
                withContext(Dispatchers.Main) {
                    onConnected(Result.failure(it))
                }
                Log.e(TAG, "connectDevice: connect fail!", it)
            })
        }
    }

    fun cancelConnect(mmSocket: BluetoothSocket?) {
        try {
            mmSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the client socket", e)
        }
    }

    suspend fun startBtReceiveServer(
        mmSocket: BluetoothSocket, onReceive: (numBytes: Int, byteBufferArray: ByteArray) -> Unit,
        onError: () -> Unit
    ) {
        keepReceive = true
        val mmInStream: InputStream = mmSocket.inputStream
        val mmBuffer = ByteArray(1024) // mmBuffer store for the stream

        withContext(Dispatchers.IO) {

            try {
                mmSocket.outputStream.write(2)
            }catch(_:Exception) {}



            var numBytes = 0 // bytes returned from read()
            while (true) {

                kotlin.runCatching {
                    mmInStream.read(mmBuffer)
                }.fold(
                    {
                        numBytes = it
                    },
                    {
                        Log.e(TAG, "Input stream was disconnected", it)
                        onError()
                        return@withContext
                    }
                )

                withContext(Dispatchers.Main) {
                    onReceive(numBytes, mmBuffer)
                }
            }
        }
    }

    fun stopBtReceiveServer() {
        keepReceive = false
    }

    suspend fun sendByteToDevice(mmSocket: BluetoothSocket, bytes: ByteArray, onSend: (result: Result<ByteArray>) -> Unit) {
        val mmOutStream: OutputStream = mmSocket.outputStream

        withContext(Dispatchers.IO) {
            val result = kotlin.runCatching {
                mmOutStream.write(bytes)
            }

            if (result.isFailure) {
                Log.e(TAG, "Error occurred when sending data", result.exceptionOrNull())
                onSend(Result.failure(result.exceptionOrNull() ?: Exception("not found exception")))
            }
            else {
                onSend(Result.success(bytes))
            }
        }
    }
}
package com.star.myapplication.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import com.star.myapplication.proto.Upload
import com.star.myapplication.utils.BtHelper
import com.star.myapplication.utils.ConnectState
import com.star.myapplication.utils.FormatUtils
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf


class DevicesVM: ViewModel() {

    companion object {
        private const val TAG = "DevicesVM"

        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DevicesVM()
        }
    }





    private var socket: BluetoothSocket? = null

    var viewStates by mutableStateOf(DevicesStates())
        private set

    fun initBt(context: Context) {
        BtHelper.instance.init(context)
        if (BtHelper.instance.checkBluetooth(context)){
            viewStates=viewStates.copy(
                pairedDevices = BtHelper.instance.queryPairDevices() ?: setOf(),
            )

        }else
        {
            viewStates=viewStates.copy(
                pairedDevices = setOf(),
            )
            showSnackBar("蓝牙权限未授予!")
        }


    }


    fun showSnackBar(msg:String){

        viewModelScope.launch {

            viewStates.snackbarHostState.showSnackbar(msg)
        }
    }

    @SuppressLint("MissingPermission")
    fun connectDevice(device: BluetoothDevice) {

        viewModelScope.launch {
            viewStates = viewStates.copy(
                connectState = ConnectState.Connecting,
                connectDevice = device,
            )

            BtHelper.instance.connectDevice(device) { it ->
                it.fold(
                    {
                        onReceivedMsg(it, device)
                    },
                    { tr ->
                        viewStates = viewStates.copy(
                            connectState = ConnectState.NotConnect,
                            connectDevice = null,
                        )

                        showSnackBar("连接失败!")


                    }
                )
            }
        }

    }

    fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

    val hchartData  = mutableListOf(entryOf(1,-1))
    val tchartData  = mutableListOf(entryOf(1,-1))

    val HchartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer()
    val TchartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer()

    @OptIn(ExperimentalSerializationApi::class)
    @SuppressLint("MissingPermission")
    private fun onReceivedMsg(bluetoothSocket: BluetoothSocket, device: BluetoothDevice) {
        socket = bluetoothSocket

        val currentBuf = ByteArray(12)
        var rc =-1
        viewModelScope.launch {
            if (socket != null) {
                BtHelper.instance.startBtReceiveServer(socket!!, onReceive = { numBytes, byteBufferArray ->
                    if (numBytes > 0) {
                        val contentArray = byteBufferArray.sliceArray(0 until numBytes)

                        Log.d(TAG,"接收到数据:${FormatUtils.bytesToHexStr(contentArray)}")

                        for ((index,e) in contentArray.withIndex()){
                            if (index>0){
                                if (e==0xcc.toByte()&&contentArray[index-1]==0xff.toByte()){
                                    try {
                                        if (rc>=2){

                                            val data = currentBuf.sliceArray(0..rc-2)
                                            Log.d(TAG,"解析到数据:${FormatUtils.bytesToHexStr(data)}")

                                            val decoded = ProtoBuf.decodeFromByteArray<Upload>(data)
                                            viewStates = viewStates.copy(temp = decoded.temp, humidity = decoded.humidity)
                                            hchartData.add(entryOf(hchartData.size,decoded.humidity))
                                            tchartData.add(entryOf(tchartData.size,decoded.temp))

                                            HchartEntryModelProducer.setEntries(hchartData)
                                            TchartEntryModelProducer.setEntries(tchartData)



                                        }

                                        rc=0
                                    } catch (e:Exception) {
                                        e.printStackTrace();
                                    }
                                    continue

                                }
                            }
                            if (rc>=0){
                                if (rc>=12){
                                    continue
                                }
                                currentBuf[rc]=e
                                rc++
                            }
                        }
//                        val arr = byteArrayOfInts(0x08, 0x20, 0x10, 0x4c)
//                        val decoded = ProtoBuf.decodeFromByteArray<Upload>(arr)
//                        viewStates = viewStates.copy(temp = decoded.temp, humidity = decoded.humidity)

                    }
                }) {
                    viewStates = viewStates.copy(
                        connectState = ConnectState.NotConnect,
                        connectDevice = null
                    )
                }
            }
        }

        viewStates = viewStates.copy(
            connectState = ConnectState.AlreadyConnected,
        )
        showSnackBar("连接设备成功!")
    }



    fun disConnectDevice() {
        BtHelper.instance.cancelConnect(socket)
        viewStates=viewStates.copy(
            connectState = ConnectState.NotConnect,
        )
    }


}

data class DevicesStates(
    val connectState: ConnectState = ConnectState.NotConnect,
    val pairedDevices: Set<BluetoothDevice> = setOf(),
    val connectDevice: BluetoothDevice? = null,
    val temp: Int = -1,
    val humidity: Int = -1,
    val snackbarHostState: SnackbarHostState = SnackbarHostState()

)
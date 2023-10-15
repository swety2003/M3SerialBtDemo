package com.star.myapplication.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast


@SuppressLint("MissingPermission")
class BluetoothStateBroadcastReceive : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        when (action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> Toast.makeText(
                context,
                "蓝牙设备:" + device!!.name + "已链接",
                Toast.LENGTH_SHORT
            ).show()

            BluetoothDevice.ACTION_ACL_DISCONNECTED -> Toast.makeText(
                context,
                "蓝牙设备:" + device!!.name + "已断开",
                Toast.LENGTH_SHORT
            ).show()

            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                val blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)
                when (blueState) {
                    BluetoothAdapter.STATE_OFF -> Toast.makeText(
                        context,
                        "蓝牙已关闭",
                        Toast.LENGTH_SHORT
                    ).show()

                    BluetoothAdapter.STATE_ON -> Toast.makeText(
                        context,
                        "蓝牙已开启",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
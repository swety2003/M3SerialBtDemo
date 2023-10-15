package com.star.myapplication.view

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.ui.graphics.vector.ImageVector
import com.star.myapplication.R

public sealed class Screen(val route: String, val ico: ImageVector, @StringRes val resourceId: Int,val title: String) {
    object Devices : Screen("devices", Icons.Rounded.Home, R.string.devices,"设备状态")
    object Connections : Screen("connections", Icons.Rounded.Share,R.string.connection,"设备管理")
    object Settings : Screen("settings", Icons.Rounded.Settings,R.string.settings,"关于")

}

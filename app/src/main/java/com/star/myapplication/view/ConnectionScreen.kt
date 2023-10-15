package com.star.myapplication.view

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.star.myapplication.utils.ConnectState
import com.star.myapplication.viewmodel.DevicesVM


@Composable
fun SmartIcon(vm: DevicesVM) {

    val color: Color =when(vm.viewStates.connectState){
        ConnectState.NotConnect-> MaterialTheme.colorScheme.errorContainer
        ConnectState.AlreadyConnected-> MaterialTheme.colorScheme.primaryContainer
        else -> {
            MaterialTheme.colorScheme.errorContainer
        }
    }
    val icon : ImageVector  = when(vm.viewStates.connectState){
        ConnectState.NotConnect-> Icons.Default.Warning
        ConnectState.AlreadyConnected-> Icons.Default.CheckCircle
        else -> {
            Icons.Default.CheckCircle
        }
    }
    Icon(imageVector =  icon, contentDescription = "")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectonView(navController: NavHostController) {

    val vm = DevicesVM.instance

    val context = LocalContext.current
//    DisposableEffect(Unit) {
//
//        vm.initBt(context)
//
//        onDispose {  }
//    }

//    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val openBottomSheet by rememberSaveable { mutableStateOf(false) }
    Scaffold(

//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { vm.initBt(context) }
            ) {
                Icon(imageVector= Icons.Rounded.Refresh,contentDescription = null)
            }
        },
        ) { innerPadding ->

        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxWidth()) {


            if (vm.viewStates.connectState==ConnectState.Connecting){
                LinearProgressIndicator(modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth())

            }

            FeatureThatRequiresCameraPermission()

            DeviceList(vm.viewStates.pairedDevices,
                vm.viewStates.connectState,
                vm.viewStates.connectDevice,
                ){
                vm.connectDevice(it)
            }

        }

    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun FeatureThatRequiresCameraPermission() {

    // Camera permission state
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.BLUETOOTH_CONNECT
    )

    if (cameraPermissionState.status.isGranted) {

        //Text("蓝牙 permission Granted")
    } else {
        Column {
            val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission
                "The Bluetooth is important for this app. Please grant the permission."
            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                "Bluetooth permission required for this feature to be available. " +
                        "Please grant the permission"
            }
            Card(modifier = Modifier.padding(8.dp),

                colors = CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.errorContainer)
            ) {

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {

                    Icon(Icons.Default.Warning, contentDescription = "")

                    Column(modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)) {

                        Text(textToShow,modifier = Modifier.padding(8.dp))


                        Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                            Text("Request permission")
                        }

                    }
                }


            }
        }
    }
}


@Composable
fun DeviceList(pairDevices: Set<BluetoothDevice>,
               connectState: ConnectState,
               connectDevice: BluetoothDevice?,
               onClickItem: (device: BluetoothDevice) -> Unit) {


    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        pairDevices.forEach { item ->
            item(key = item) {
                ScanResultItem(item,onClickItem)
            }
        }

    }
}

@SuppressLint("MissingPermission")
    @Composable
    fun ScanResultItem(item: BluetoothDevice, onClickItem: (device: BluetoothDevice) -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onClickItem(item) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(Icons.Default.Place, contentDescription = "")
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "${item.name} ",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Text(
                            text = "MAC:${item.address}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )

                    }


                }

            }
        }
    }


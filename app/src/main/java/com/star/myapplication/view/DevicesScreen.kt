package com.star.myapplication.view

import android.annotation.SuppressLint
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.ChartScrollState
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.core.scroll.AutoScrollCondition
import com.patrykandpatrick.vico.core.scroll.InitialScroll
import com.star.myapplication.utils.ConnectState
import com.star.myapplication.viewmodel.DevicesVM
import kotlin.random.Random

@Composable
fun DeviceView(navController: NavHostController) {

    val vm =DevicesVM.instance

    Scaffold(
        content = { innerPadding ->

            val scrollState = rememberScrollState()

            Column(modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(16.dp))
                {

                DeviceStateCard(DevicesVM.instance)

                Row (horizontalArrangement = Arrangement.SpaceBetween){

                    OutlinedCard (modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)){
                        Row(modifier = Modifier
                            .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DeviceThermostat, contentDescription = "", tint = Color(0xFFDB6841))

                            Column(modifier = Modifier
                                .padding(horizontal = 16.dp)) {
                                Text(text = "${vm.viewStates.temp}", style = MaterialTheme.typography.headlineLarge, color =MaterialTheme.colorScheme.primary)

                                Text(text = "温度 ℃", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)

                            }
                        }
                    }

                    OutlinedCard(modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Water, contentDescription = "", tint = Color(0xFF3795DB))

                            Column(modifier = Modifier
                                .padding(horizontal = 16.dp)) {
                                Text(text = "${vm.viewStates.humidity}", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)

                                Text(text = "湿度(rh)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)

                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                ChartView1(vm)
            }


        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun DeviceStateCard(vm:DevicesVM){
    val color: Color =when(vm.viewStates.connectState){
        ConnectState.NotConnect-> MaterialTheme.colorScheme.errorContainer
        ConnectState.AlreadyConnected-> MaterialTheme.colorScheme.primary
        else -> {
            MaterialTheme.colorScheme.errorContainer
        }
    }
    val icon : ImageVector = when(vm.viewStates.connectState){
        ConnectState.NotConnect-> Icons.Default.Warning
        ConnectState.AlreadyConnected-> Icons.Default.CheckCircle
        else -> {
            Icons.Default.QuestionMark
        }
    }

    val openDialog = remember { mutableStateOf(false) }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openDialog.value = false
            }
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Column (modifier = Modifier.fillMaxWidth() ,horizontalAlignment = Alignment.CenterHorizontally){
                        Icon(Icons.Default.QuestionMark, contentDescription = "")

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "提示"
                            , style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "确认要断开与${vm.viewStates.connectDevice?.name}的连接吗?",color=MaterialTheme.colorScheme.secondary

                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(
                        onClick = {
                            openDialog.value = false

                            vm.disConnectDevice()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("确认")
                    }
                }
            }
        }
    }

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
        .clickable {
            if (vm.viewStates.connectState == ConnectState.AlreadyConnected) {
                openDialog.value = true
            }
        },
        colors = CardDefaults.cardColors(containerColor=color)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {

            Icon(icon, contentDescription = "")

            Column(modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text(text = "${vm.viewStates.connectDevice?.name} ${vm.viewStates.connectState}", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)

                vm.viewStates.connectDevice?.let { Text(text = it.address, style = MaterialTheme.typography.labelMedium) }

            }
        }
    }

}


@Composable
fun ChartView1(vm:DevicesVM){

        Card {

            Column(modifier = Modifier.padding(4.dp)) {

                val chartScrollSpec = rememberChartScrollSpec(
                    isScrollEnabled = true,
                    initialScroll = InitialScroll.End,
                    autoScrollCondition = AutoScrollCondition.OnModelSizeIncreased,
                    autoScrollAnimationSpec = spring()
                )

                val chartScrollState = rememberChartScrollState()


                Chart(
                    chart = lineChart(),
                    chartModelProducer = vm.TchartEntryModelProducer,
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(),
                    modifier = Modifier.padding(8.dp),
                    chartScrollState = chartScrollState,
                    chartScrollSpec = chartScrollSpec,
                    diffAnimationSpec = snap()
                )
                Text(text = "温度",modifier = Modifier.align(alignment = Alignment.CenterHorizontally))


                Spacer(modifier = Modifier.height(6.dp))
                Chart(
                    chart = lineChart(),
                    chartModelProducer = vm.HchartEntryModelProducer,
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(),
                    modifier = Modifier.padding(8.dp),

                    chartScrollState = chartScrollState,
                    chartScrollSpec = chartScrollSpec,
                    diffAnimationSpec = snap()


                )
                Text(text = "湿度 ",modifier = Modifier.align(alignment = Alignment.CenterHorizontally))

                Spacer(modifier = Modifier.height(8.dp))
            }
        }


}
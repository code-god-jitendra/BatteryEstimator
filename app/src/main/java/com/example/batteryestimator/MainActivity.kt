package com.example.batteryestimator

import android.content.Context
import android.os.BatteryManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.delay
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BatteryEstimatorScreen()
        }
    }
}

@Composable
fun BatteryEstimatorScreen() {
    val context = LocalContext.current
    val batteryManager = remember {
        context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    }

    // States for display
    var currentNow by remember { mutableStateOf(0L) }      // µA
    var chargeCounter by remember { mutableStateOf(0L) }   // µAh
    var hoursLeft by remember { mutableStateOf(0f) }       // hours

    // History for charting: List of (minuteIndex, currentNow)
    val history = remember { mutableStateListOf<Entry>() }
    var minuteIndex by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            // Read instantaneous current (µA) and remaining charge (µAh)
            currentNow = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
                .let { if (it < 0) -it else it } // API returns negative for discharge
            chargeCounter = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
                .let { if (it < 0) -it else it }

            // Compute hours left (guard against zero current)
            hoursLeft = if (currentNow > 0) {
                chargeCounter.toFloat() / currentNow.toFloat()
            } else 0f

            // Record for chart
            history.add(Entry(minuteIndex, currentNow.toFloat() / 1000f)) // convert µA to mA
            if (history.size > 60) {
                history.removeAt(0)
            }
            minuteIndex += 1f

            // Wait 60 seconds before next sample
            delay(60_000L)
        }
    }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text("🔋 Battery Estimator", fontSize = 28.sp)
            Spacer(Modifier.height(24.dp))
            Text("Current Draw: ${"%.0f".format(currentNow / 1000f)} mA", fontSize = 20.sp)
            Spacer(Modifier.height(8.dp))
            Text("Remaining Charge: ${chargeCounter / 1000} mAh", fontSize = 20.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "Estimated Time Left: ${"%.1f".format(hoursLeft)} hr",
                fontSize = 20.sp
            )
            Spacer(Modifier.height(24.dp))

            // Live line chart of current draw over time
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                factory = { ctx ->
                    LineChart(ctx).apply {
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        axisRight.isEnabled = false
                        description.isEnabled = false
                    }
                },
                update = { chart ->
                    val dataSet = LineDataSet(history, "mA Draw").apply {
                        setDrawCircles(false)
                        lineWidth = 2f
                    }
                    chart.data = LineData(dataSet)
                    chart.xAxis.labelRotationAngle = -45f
                    chart.invalidate()
                }
            )
        }
    }
}

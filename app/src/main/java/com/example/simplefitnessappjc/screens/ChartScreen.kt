package com.example.simplefitnessappjc.screens

import android.util.Log
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.navigation.NavController
import com.example.simplefitnessappjc.R
import com.example.simplefitnessappjc.model.MainViewModel
import com.example.simplefitnessappjc.rememberMarker
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.Axis
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.scroll.InitialScroll
import com.patrykandpatrick.vico.core.scroll.ScrollListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun ChartScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val modelProducer = remember { ChartEntryModelProducer() }
    val dataSetLineSpec = remember { arrayListOf<LineChart.LineSpec>() }
    val scrollState = rememberChartScrollState()

    val pulseEntries by viewModel.pulseDataSet.collectAsState()
    var loadData by remember { mutableStateOf(false) }

    val baseTimestamp = viewModel.baseTimestamp

    LaunchedEffect(loadData) {
        if (loadData) {
            while (isActive) {
                viewModel.getFitnessData()
                delay(1000)
                if (pulseEntries.isNotEmpty()) {
                    modelProducer.setEntries(pulseEntries)
                    Log.i("ChartScreen", "Einträge: $pulseEntries")
                    val latestEntry = pulseEntries.last()
                    scrollState.scrollBy(latestEntry.x)
                }
            }
        } else {
            viewModel.clearPulseData()
        }
    }


    dataSetLineSpec.add(
        LineChart.LineSpec(
            lineColor = Blue.toArgb()
        )
    )

    ProvideChartStyle {
        Column(modifier = Modifier.fillMaxSize()) {
            Chart(
                modifier = Modifier.weight(1f),
                chart = lineChart(
                    lines = dataSetLineSpec,
                ),
                marker = rememberMarker(),
                chartModelProducer = modelProducer,
                startAxis = rememberStartAxis(
                    title = "",
                    tickLength = 0.dp,
                    valueFormatter = { value, _ ->
                        ((value.toInt()) + 1).toString()
                    },
                    itemPlacer = AxisItemPlacer.Vertical.default(
                        maxItemCount = 6
                    )
                ),
                bottomAxis = rememberBottomAxis(
                    title = "",
                    tickLength = 0.dp,
                    valueFormatter = { value, _ ->
                        val seconds = value.toLong()
                        val dateTime = baseTimestamp.plusSeconds(seconds)
                        val formattedTime = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                        Log.i("ChartScreen", "Zeit: $formattedTime")
                        formattedTime
                    },
                    itemPlacer = AxisItemPlacer.Horizontal.default(

                    )
                ),
                chartScrollState = scrollState,
                isZoomEnabled = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { loadData = !loadData }
            ) {
                Text(
                    text = if (loadData) {
                        stringResource(R.string.stop_load_data)
                    } else {
                        stringResource(R.string.start_load_data)
                    }
                )
            }
        }
    }
}
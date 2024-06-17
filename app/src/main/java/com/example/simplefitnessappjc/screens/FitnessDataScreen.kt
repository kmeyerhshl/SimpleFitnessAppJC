package com.example.simplefitnessappjc.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.simplefitnessappjc.R
import com.example.simplefitnessappjc.model.MainViewModel
import com.example.simplefitnessappjc.network.ErrorCodes
import com.github.anastr.speedometer.SpeedView
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun FitnessDataScreen(
    navController: NavController,
    viewModel: MainViewModel
) {

    val fitnessData = viewModel.fitnessData.value
    var dataIsLoading by remember { mutableStateOf(false) }

    var speed by remember { mutableStateOf(30f) }
    val currentSpeed by animateFloatAsState(
        targetValue = speed,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(dataIsLoading) {
        if (dataIsLoading) {
            while (isActive) {
                viewModel.getFitnessData()
                delay(1000) // 1 Sekunde Verzögerung
            }
        } else {
            speed = 30f
        }
    }

    LaunchedEffect(fitnessData) {
        if (fitnessData.errorcode == ErrorCodes.NO_ERROR) {
            speed = fitnessData.puls.toFloat()
        } else {
            speed = 30f
        }
    }

    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpeedView(
            modifier = Modifier.size(250.dp),
            speed = currentSpeed,
            minSpeed = 30F,
            maxSpeed = 170F
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { dataIsLoading = !dataIsLoading }) {
            Text(text = if (dataIsLoading) {
                stringResource(R.string.stop_load_data)
            } else {
                stringResource(R.string.start_load_data)
            })
        }
    }
}
package com.example.simplefitnessappjc.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.simplefitnessappjc.R
import com.example.simplefitnessappjc.model.MainViewModel
import com.example.simplefitnessappjc.screens.ChartScreen
import com.example.simplefitnessappjc.screens.FitnessDataScreen

sealed class MyNavDestination(
    val route: String,
    val title: Int = 0,
    val label: Int = 0,
    val content: @Composable (NavController, MainViewModel) -> Unit
) {

    // hier alle Bildschirme mit den notwendigen Infos dazu listen

    // BottomNavScreens

    object FitnessData : MyNavDestination(
        route = "fitnessData",                          // eindeutige Kennung
        title = R.string.fitnessDataScreenTitle,        // Titel in der TopBar
        label = R.string.fitnessDataScreenLabel,        // Label in der BottomBar
        content = { navController, viewModel -> FitnessDataScreen(navController, viewModel) }
    )

    object Chart : MyNavDestination(
        route = "chart",
        title = R.string.chartScreenTitle,
        label = R.string.chartScreenLabel,
        content = { navController, viewModel -> ChartScreen(navController, viewModel) }
    )


}




val navDestinations = listOf (
    MyNavDestination.FitnessData,
    MyNavDestination.Chart
)
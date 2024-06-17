package com.example.simplefitnessappjc.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.simplefitnessappjc.model.MainViewModel
import com.example.simplefitnessappjc.navigation.MyMenu
import com.example.simplefitnessappjc.navigation.MyNavDestination
import com.example.simplefitnessappjc.navigation.MyTopBar
import com.example.simplefitnessappjc.navigation.navDestinations

@Composable
fun MyApp() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        snackbarHost = { SnackbarHost(snackbarHostState) {
            Snackbar(
                snackbarData = it,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                actionColor = MaterialTheme.colorScheme.onPrimaryContainer,
                dismissActionContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } },
        topBar = {
            MyTopBar(
                navController = navController,
                screens = navDestinations,
                onMenuClick = { showMenu = !showMenu },
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = MyNavDestination.FitnessData.route,
            modifier = Modifier
                .padding(paddingValues)
        ) {
            // Screens via BottomBar und Fullscreens
            navDestinations.forEach { screen ->
                composable(screen.route) {
                    screen.content(navController, viewModel)
                }
            }
        }
        MyMenu(
            showMenu = showMenu,
            navController = navController,
            paddingValues = paddingValues,
            onToggleMenu = { showMenu = !showMenu }
        )
    }
}
package com.example.simplefitnessappjc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.simplefitnessappjc.screens.MyApp
import com.example.simplefitnessappjc.ui.theme.SimpleFitnessAppJCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleFitnessAppJCTheme {
                MyApp()
            }
        }
    }
}
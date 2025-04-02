package com.example.utsquranappq.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class HomeScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController() // Buat NavController

            Scaffold(
                bottomBar = { BottomNavigationBar(navController) } // BottomNavigationBar selalu tampil
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.padding(paddingValues) // Hindari overlap dengan bottom bar
                ) {
                    composable("home") { HomeScreen(navController) }
                    composable("quran") { DoaScreen() }
                }
            }
        }
    }
}


package com.example.utsquranappq.navigation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.utsquranappq.ui.BottomNavigationBar
import com.example.utsquranappq.ui.DoaScreen
import com.example.utsquranappq.ui.HomeScreen
import com.example.utsquranappq.ui.QiblaCompassScreen
import com.example.utsquranappq.ui.SurahDetailScreen
import com.example.utsquranappq.ui.SurahTab

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
                    composable("Qiblat") { QiblaCompassScreen() }
                    composable("surahTab") { SurahTab(navController) }
                    composable("surahDetail/{surahNumber}") { backStackEntry ->
                        val surahNumber = backStackEntry.arguments?.getString("surahNumber")?.toIntOrNull()
                        Log.d("Navigation", "Navigating to SurahDetailScreen with number: $surahNumber")
                        SurahDetailScreen(surahNumber, navController)
                    }
                }
            }
        }
    }
}


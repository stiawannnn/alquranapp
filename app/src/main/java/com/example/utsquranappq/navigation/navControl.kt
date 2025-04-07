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
import com.example.utsquranappq.ui.juzuI.juzzdetailscreen.JuzDetailScreen
import com.example.utsquranappq.ui.*
import com.example.utsquranappq.ui.juzuI.juzscreen.JuzTab
import com.example.utsquranappq.ui.surahui.surahdetailscreen.SurahDetailScreen
import com.example.utsquranappq.ui.surahui.surahscreen.SurahTab

class HomeScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable("home") { HomeScreen(navController) }
                    composable("info") { TajwidScreen() }
                    composable("sholat") { JadwalSholat() }
                    composable("bookmark") { BookmarkScreen() }
                    composable("Qiblat") { QiblaCompassScreen() }
                    composable("surahTab") { SurahTab(navController) }
                    composable("surahDetail/{surahNumber}") { backStackEntry ->
                        val surahNumber = backStackEntry.arguments?.getString("surahNumber")?.toIntOrNull()
                        Log.d("Navigation", "Navigating to SurahDetailScreen with number: $surahNumber")
                        SurahDetailScreen(surahNumber, navController)
                    }
                    composable("juz_list") {
                        JuzTab(navController = navController)
                    }
                    composable("juz_detail/{juzNumber}") { backStackEntry ->
                        val juzNumber = backStackEntry.arguments?.getString("juzNumber")?.toIntOrNull()
                        JuzDetailScreen(juzNumber = juzNumber, navController = navController)
                    }
                }
            }
        }
    }
}

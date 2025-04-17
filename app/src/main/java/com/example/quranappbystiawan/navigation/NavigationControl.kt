package com.example.quranappbystiawan.navigation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quranappbystiawan.activity.HomeScreen
import com.example.quranappbystiawan.ui.juzui.juzzdetailscreen.JuzDetailScreen
import com.example.quranappbystiawan.ui.*
import com.example.quranappbystiawan.ui.juzui.juzscreen.JuzTab
import com.example.quranappbystiawan.ui.surahui.surahdetailscreen.SurahDetailScreen
import com.example.quranappbystiawan.ui.surahui.surahscreen.SurahTab

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
                    composable("sholat") { JadwalSholat(navController) }
                    composable("bookmark") { BookmarkScreen(navController) }
                    composable("Qiblat") { QiblaCompassScreen() }
                    composable("surahTab") { SurahTab(navController) }
                    composable(
                        route = "surahDetail/{surahNumber}?ayah={ayah}",
                        arguments = listOf(
                            navArgument("surahNumber") { type = NavType.IntType },
                            navArgument("ayah") { type = NavType.StringType; nullable = true }
                        )
                    ) { backStackEntry ->
                        val surahNumber = backStackEntry.arguments?.getInt("surahNumber")
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
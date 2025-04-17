package com.example.quranappbystiawan.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quranappbystiawan.R

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(containerColor = Color(0xFF150D23)) {
        val items = listOf("Jadwal", "Qiblat", "Quran", "Bookmark", "Tajweed")
        val icons = listOf(
            R.drawable.sholat, R.drawable.navigasi,
            R.drawable.qur5, R.drawable.bookmarks, R.drawable.tajweed
        )
        var selectedItem by remember { mutableStateOf(2) }

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Image(
                        painter = painterResource(id = icons[index]),
                        contentDescription = item,
                        modifier = Modifier.size(23.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    when (index) {
                        0 -> navController.navigate("sholat")
                        1 -> navController.navigate("Qiblat")
                        2 -> navController.navigate("home")
                        3 -> navController.navigate("bookmark")
                        4 -> navController.navigate("info")
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF03A9F4),
                    unselectedIconColor = Color(0xFFB0BEC5),
                    indicatorColor = Color(0xFF0B2A80),
                    selectedTextColor = Color(0xFF2A7FE0),
                    unselectedTextColor = Color(0xFFB0BEC5)
                )
            )
        }
    }
}
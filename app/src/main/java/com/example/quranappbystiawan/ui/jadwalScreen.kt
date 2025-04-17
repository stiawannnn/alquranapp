package com.example.quranappbystiawan.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quranappbystiawan.R

@Composable
fun JadwalSholat(navController: NavController) {
    val backgroundColor = Brush.verticalGradient(
        colors = listOf(Color(0xFF3E1B8F), Color(0xFF72308A), Color(0xFFB86AD7))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TopBar(navController)
            Spacer(modifier = Modifier.height(16.dp))
            MosqueImage()
            Spacer(modifier = Modifier.height(16.dp))
            PrayerCard(name = "Subuh", time = "04:59 WIB", icon = R.drawable.subuh)
            PrayerCard(name = "Dhuzur", time = "12:20 WIB", icon = R.drawable.zhur)
            PrayerCard(name = "Ashr", time = "15:30 WIB", icon = R.drawable.asr)
            PrayerCard(name = "Maghrib", time = "18:23 WIB", icon = R.drawable.magrib)
            PrayerCard(name = "Isha", time = "19:32 WIB", icon = R.drawable.isyah)
        }
    }
}

@Composable
fun TopBar(navController: NavController) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {
                navController.navigate("home")
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Jadwal Sholat", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = Color.White
            )
        }
        Text(
            "Kota Pekanbaru Dan Sekitarnya, Indonesia",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 48.dp, top = 4.dp)
        )
    }
}

@Composable
fun MosqueImage() {
    Image(
        painter = painterResource(id = R.drawable.masjid1),
        contentDescription = "Mosque",
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .fillMaxWidth()
            .height(277.dp)
    )
}

@Composable
fun PrayerCard(name: String, time: String, icon: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = name,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.weight(1f))
            Text(time, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
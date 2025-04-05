package com.example.utsquranappq.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.utsquranappq.R
import com.example.utsquranappq.navigation.HomeScreenActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, HomeScreenActivity::class.java))
            finish()
        }, 3000) // Delay 3 detik sebelum pindah ke home
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2A41BE), // Biru muda
                        Color(0xFF2AB8D0), // Kuning muda
                        Color(0xFF9B3CBD) // Biru Muda
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash),
                contentDescription = "App Logo",
                modifier = Modifier.size(233.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "My Quran App",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Dendi Setiawan",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Gambar masjid di bagian bawah layar, memenuhi kiri & kanan
        Image(
            painter = painterResource(id = R.drawable.ui), // Pastikan gambar ada di drawable
            contentDescription = "Masjid",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter) // Perbaikan di sini!
        )
    }
}

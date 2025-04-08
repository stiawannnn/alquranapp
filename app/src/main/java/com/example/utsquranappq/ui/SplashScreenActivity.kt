package com.example.utsquranappq.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, HomeScreenActivity::class.java))
            finish()
        }, if (allGranted) 3000 else 5000) // Delay lebih lama jika izin ditolak untuk beri waktu baca
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()
        }

        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            permissions.add(Manifest.permission.USE_EXACT_ALARM)
        }

        if (permissions.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissions.toTypedArray())
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, HomeScreenActivity::class.java))
                finish()
            }, 3000)
        }
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
                        Color(0xFF2A41BE),
                        Color(0xFF2AB8D0),
                        Color(0xFF9B3CBD)
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
        Image(
            painter = painterResource(id = R.drawable.ui),
            contentDescription = "Masjid",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}
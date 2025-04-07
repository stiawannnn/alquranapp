
package com.example.utsquranappq.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BookmarkScreen() {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2A41BE), // Biru muda
            Color(0xFF2AB8D0), // Kuning muda (tapi sebenarnya lebih ke biru toska)
            Color(0xFF9B3CBD)  // Ungu muda
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush) // 👈 Gradient background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Coming Soon",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White // Biar teks kelihatan di atas gradient
        )
    }
}


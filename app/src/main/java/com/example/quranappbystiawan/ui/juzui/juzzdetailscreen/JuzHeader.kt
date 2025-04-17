package com.example.quranappbystiawan.ui.juzui.juzzdetailscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quranappbystiawan.R
import androidx.compose.ui.unit.sp
import com.example.quranappbystiawan.utils.getTranslation

@Composable
fun SurahHeaderBox(
    surahNumber: Int,
    englishName: String,
    englishTranslation: String,
    revelationType: String,
    numberOfAyahs: Int,
) {
    val (surahIndo, meaningIndo, revelationIndo) = getTranslation(
        englishName,
        englishTranslation,
        revelationType
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 11.dp, vertical = 7.dp)
            .fillMaxWidth()
            .height(188.dp)
            .background(
                brush = Brush.linearGradient(
                    listOf(Color(0xFF673AB7), Color(0xFF635A9F), Color(0xFF03A9F4))
                ),
                shape = RoundedCornerShape(33.dp)
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text(
                text = surahIndo,
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = meaningIndo,
                color = Color.White,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(11.dp))
            Text(
                text = "$revelationIndo • $numberOfAyahs Ayat",
                color = Color.White,
                fontSize = 12.sp
            )
        }

        // Bismillah tetap di paling bawah
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {

            // Bismillah hanya ditampilkan jika bukan Surah Al-Fatihah (nomor 1)
            if (surahNumber != 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bismillah),
                        contentDescription = "Bismillah",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(top = 88.dp)
                    )
                }
            }
        }
    }
}
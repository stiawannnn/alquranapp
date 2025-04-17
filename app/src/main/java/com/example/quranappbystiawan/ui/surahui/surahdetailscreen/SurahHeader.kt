package com.example.quranappbystiawan.ui.surahui.surahdetailscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quranappbystiawan.R
import com.example.quranappbystiawan.model.AyahEdition
import com.example.quranappbystiawan.model.Surah
import com.example.quranappbystiawan.utils.getTranslation

//manggil fungsi di viewmodel untuk menerima parameter
@Composable
fun SurahHeader(
    currentSurah: Surah?,
    surahDetail: List<AyahEdition>
) {
    val juzNumber = surahDetail.firstOrNull()?.juz?.toString() ?: "Unknown"

    val (namaSurah, artiSurah, jenisWahyu) = getTranslation(
        currentSurah?.englishName ?: "",
        currentSurah?.englishNameTranslation ?: "",
        currentSurah?.revelationType ?: ""
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 11.dp, vertical = 7.dp)
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF673AB7), Color(0xFF635A9F), Color(0xFF03A9F4))
                ),
                shape = RoundedCornerShape(23.dp)
            )
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.qur3),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center).size(244.dp).alpha(0.2f)
        )

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))
            currentSurah?.let {
                Text(
                    text = "Juz : $juzNumber",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = namaSurah,
                    color = Color.White,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = artiSurah,
                    color = Color.White
                )
                Text(
                    text = "$jenisWahyu • ${it.numberOfAyahs} Ayat",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (currentSurah?.number != 1 && currentSurah?.number != 9) {
                Image(
                    painter = painterResource(id = R.drawable.bismillah),
                    contentDescription = "Bismillah",
                    modifier = Modifier.size(223.dp)
                )
            }
        }
    }
}
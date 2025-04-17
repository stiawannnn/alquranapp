package com.example.quranappbystiawan.activity.homescreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quranappbystiawan.R
import com.example.quranappbystiawan.model.Surah
import com.example.quranappbystiawan.utils.getTranslation
import kotlinx.coroutines.delay

@Composable
fun GreetingSection(
    lastSurah: Int, lastAyah: Int, lastJuz: Int, lastJuzSurah: Int, lastJuzAyah: Int,
    surahList: List<Surah>, navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 11.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "السَّلاَمُ عَلَيْكُمْ",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.hafs)),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Image(
            painter = painterResource(id = R.drawable.quran1),
            contentDescription = "Greeting Icon",
            modifier = Modifier
                .size(107.dp)
                .padding(top = 0.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LastSeenSection(lastSurah, lastAyah, lastJuz, lastJuzSurah, lastJuzAyah, surahList, navController)
    }
}

@Composable
fun LastSeenSection(
    lastSurah: Int,
    lastAyah: Int,
    lastJuz: Int,
    lastJuzSurah: Int,
    lastJuzAyah: Int,
    surahList: List<Surah>,
    navController: NavController
) {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = getCurrentTime()
        }
    }

    val lastSeenText = when {
        lastSurah != -1 && lastAyah != -1 -> {
            val surahName = surahList.find { it.number == lastSurah }
                ?.let { getTranslation(it.englishName, "", "").first } ?: "Unknown"
            "$surahName - Ayat $lastAyah"
        }
        lastJuz != -1 && lastJuzSurah != -1 && lastJuzAyah != -1 -> {
            val surahName = surahList.find { it.number == lastJuzSurah }
                ?.let { getTranslation(it.englishName, "", "").first } ?: "Unknown"
            "Juz $lastJuz \n$surahName"
        }
        else -> "Belum ada riwayat"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(144.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF673AB7), Color(0xFFC49CC9), Color(0xFFC804EA))
                ),
                shape = RoundedCornerShape(23.dp)
            )
            .clickable {
                when {
                    lastSurah != -1 && lastAyah != -1 -> {
                        Log.d("LastSeenSection", "Navigating to surahDetail/$lastSurah?ayah=$lastAyah")
                        navController.navigate("surahDetail/$lastSurah?ayah=$lastAyah")
                    }
                    lastJuz != -1 -> navController.navigate("juz_detail/$lastJuz")
                }
            }
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Terakhir Dibaca",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = lastSeenText,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable {
                                when {
                                    lastSurah != -1 && lastAyah != -1 -> {
                                        Log.d("LastSeenSection", "Navigating to surahDetail/$lastSurah?ayah=$lastAyah")
                                        navController.navigate("surahDetail/$lastSurah?ayah=$lastAyah")
                                    }
                                    lastJuz != -1 -> navController.navigate("juz_detail/$lastJuz")
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lanjutkan",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.arrow1),
                            contentDescription = "Icon Lanjutkan",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = currentTime,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
        }
    }
}
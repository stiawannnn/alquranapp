package com.example.utsquranappq.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TajwidScreen() {
    val tajwidList = listOf(
        TajwidItem(
            title = "Qalqalah",
            description = """
            Saat salah satu huruf (ق ط ب ج د) mati (sukun) di tengah atau akhir ayat.
            - Qalqalah Sughra: di tengah kalimat.
            - Qalqalah Kubra: di akhir kalimat saat waqaf.
            Suara huruf dipantulkan saat dibaca.
        """.trimIndent(),
            example = "اقْرَأْ بِاسْمِ رَبِّكَ الَّذِي خَلَقَ",
            highlight = "قْ",
            color = Color(0xFF12B266)
        ),
        TajwidItem(
            title = "Ikhfa Syafawi",
            description = """
            Ketika huruf Mim mati مْ bertemu dengan huruf Ba ب.
            Hukum Ikhfa Syafawi menyamarkan huruf mim mati seperti huruf MNG dan ditahan 2 harakat sebelum menyebut huruf Ba.
        """.trimIndent(),
            example = "لَسْتَ عَلَيْهِمْ بِمُصَيْطِرٍ",
            highlight = "مْ بِ",
            color = Color(0xFFE288F6)
        ),
        TajwidItem(
            title = "Ikhfa",
            description = """
        Ketika huruf Nun mati ن atau Tanwin bertemu dengan salah satu dari 15 huruf (ت ث ج د ذ ز س ش ص ض ط ظ ف ق ك).
        Cara bacanya disamarkan seperti NG dan ditahan 2 harakat.

        Ikhfa dibagi:
        1. Aqrob – samar ringan (د ط ت)
        2. Ausath – samar sedang (ث ج ذ ز س ش ص ض ظ ف)
        3. Ab’ad – samar berat (ق ك)
    """.trimIndent(),
            example = "وَكُنتُمْ أَمْوَاتًا فَأَحْيَاكُمْ",
            highlight = "كُنتُمْ", // ← bagian yang kena tajwid
            color = Color(0xFFEE0606)
        ),

        TajwidItem(
            title = "Idgham Syafawi",
            description = """
            Terjadi saat Mim mati مْ bertemu dengan huruf Mim م.
            Dibaca dengan menyatu dan mendengung (disebut juga Idgham Mimi).
        """.trimIndent(),
            example = "هُمْ مِّنْهُمْ",
            highlight = "مْ مِّ",
            color = Color(0xFF58B800)
        ),
        TajwidItem(
            title = "Iqlab",
            description = """
            Terjadi ketika Nun mati ن atau Tanwin bertemu huruf Ba ب.
            Dibaca dengan mengganti ن menjadi suara م dan didengungkan.
        """.trimIndent(),
            example = "يُنْبِتُ لَكُمْ",
            highlight = "نْبِ",
            color = Color(0xFF40C4FF)
        ),
        TajwidItem(
            title = "Idgham Bighunnah",
            description = """
            Terjadi saat Nun mati ن atau Tanwin bertemu huruf ي ن م و.
            Dibaca menyatu dengan dengung.
        """.trimIndent(),
            example = "مِنْ نِعْمَتِهِ",
            highlight = "نْ نِ",
            color = Color(0xFFA152DC)
        ),
        TajwidItem(
            title = "Idgham Bilaghunnah",
            description = """
            Terjadi saat Nun mati ن atau Tanwin bertemu huruf ل atau ر.
            Dibaca menyatu tanpa dengung.
        """.trimIndent(),
            example = "مِنْ رَبِّهِمْ",
            highlight = "نْ رَ",
            color = Color(0xFF8B948B)
        ),
        TajwidItem(
            title = "Ghunna",
            description = """
            Ghunna adalah suara dengung yang muncul saat membaca Nun (ن) atau Mim (م) bertasydid.
            Dengung ini ditahan sekitar 2 harakat.
        """.trimIndent(),
            example = "إِنَّ اللَّهَ غَفُورٌ رَحِيمٌ",
            highlight = "نَّ",
            color = Color(0xFFFF7E1E)
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        items(tajwidList) { item ->
            TajwidCardFull(item)
        }
    }
}

@Composable
fun TajwidCardFull(item: TajwidItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.Black, shape = RoundedCornerShape(16.dp))
            .padding(bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(item.color, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Text(
                text = item.title,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = item.description,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = getAnnotatedExample(item.example, item.highlight, item.color),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 33.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun getAnnotatedExample(example: String, highlight: String, color: Color): AnnotatedString {
    return buildAnnotatedString {
        val start = example.indexOf(highlight)
        if (start >= 0) {
            // Teks sebelum highlight
            withStyle(SpanStyle(color = Color.White)) {
                append(example.substring(0, start))
            }

            // Highlight berwarna tajwid
            withStyle(SpanStyle(color = color)) {
                append(highlight)
            }

            // Teks setelah highlight
            withStyle(SpanStyle(color = Color.White)) {
                append(example.substring(start + highlight.length))
            }
        } else {
            // fallback kalau highlight nggak ketemu
            withStyle(SpanStyle(color = Color.White)) {
                append(example)
            }
        }
    }
}


data class TajwidItem(
    val title: String,
    val description: String,
    val example: String,
    val highlight: String,
    val color: Color
)


package com.example.utsquranappq.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quranapp.viewmodel.SurahViewModel
import com.example.utsquranappq.R

@Composable
fun SurahTab() {
    val viewModel: SurahViewModel = viewModel()
    val surahList by viewModel.surahList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Dark mode
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn {
            items(surahList) { surah ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Box untuk menaruh teks di tengah gambar nomor surah
                        Box(
                            contentAlignment = Alignment.Center, // Teks di tengah gambar
                            modifier = Modifier.size(44.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.nomorsurah), // Ganti dengan ikon nomor surah
                                contentDescription = "Surah Number",
                                modifier = Modifier.fillMaxSize()
                            )
                            Text(
                                text = surah.number.toString(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 11.sp, // Ukuran teks
                                    color = Color.White, // Warna teks putih
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp)) // Jarak antara gambar & teks surah

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${surah.englishName}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                fontFamily = FontFamily(Font(R.font.hafs))
                            )

                            Text(
                                text = "${surah.englishNameTranslation}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Gray
                                )
                            )

                                Text(
                                    text = "${surah.revelationType.uppercase()} • ${surah.numberOfAyahs} Ayat",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.Gray
                                )
                                )
                        }

                        Text(
                            text = surah.name, // Arabic name
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color(0xFFBB86FC), // Purple accent
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

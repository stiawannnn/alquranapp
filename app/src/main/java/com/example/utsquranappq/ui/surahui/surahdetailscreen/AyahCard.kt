package com.example.utsquranappq.ui.surahui.surahdetailscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.utsquranappq.R
import com.example.utsquranappq.model.AyahEdition
import com.example.utsquranappq.utiils.parseTajweedText

@Composable
fun AyahCard(
    ayahs: List<AyahEdition>,
    selectedQari: String?,
    allAyahs: List<AyahEdition>,
    currentPlayingAyah: Int?,
    audioManager: AudioManager,
    onAyahPlaying: (Int) -> Unit,
    onPlayAll: (Int) -> Unit
) {
    val isPlayingAll = audioManager.isPlayingAll.value
    val isPaused = audioManager.isPaused.value

    // Group ayat berdasarkan numberInSurah
    val groupedAyahs = ayahs.groupBy { it.numberInSurah }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            groupedAyahs.forEach { (_, ayahGroup) ->
                val tajweedAyah = ayahGroup.find { it.edition.identifier == "quran-tajweed" }
                val transliterationAyah =
                    ayahGroup.find { it.edition.identifier == "en.transliteration" }
                val translationAyah = ayahGroup.find { it.edition.identifier == "id.indonesian" }
                val audioAyah = ayahGroup.find {
                    it.audio != null && (selectedQari == null || it.edition.identifier == selectedQari)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    // Audio Player pojok kanan atas
                    audioAyah?.let {
                        AudioPlayer(
                            audioUrl = it.audio ?: "",
                            ayahNumber = it.numberInSurah,
                            qariName = it.edition.englishName,
                            allAyahs = allAyahs,
                            selectedQari = selectedQari,
                            onAyahPlaying = onAyahPlaying,
                            isPlayingAll = isPlayingAll,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp)
                    ) {

                        // Nomor Ayat (gambar + nomor)
                        Box(
                            modifier = Modifier
                                .padding(bottom = 33.dp)
                                .align(Alignment.Start)
                        ) {
                            Box(
                                modifier = Modifier.size(44.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.nomorsurah),
                                    contentDescription = "Ayah Number",
                                    modifier = Modifier.fillMaxSize()
                                )
                                Text(
                                    text = tajweedAyah?.numberInSurah?.toString() ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF03A9F4),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Tajwid Text
                        tajweedAyah?.let {
                            val annotatedText = parseTajweedText("${it.text} ")
                            Text(
                                text = annotatedText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 33.sp,
                                    lineHeight = 66.sp,
                                    fontFamily = FontFamily(Font(R.font.scheherazadenew)),
                                    color = when {
                                        currentPlayingAyah == it.numberInSurah && !isPaused -> Color.Yellow
                                        currentPlayingAyah == it.numberInSurah && isPaused -> Color(
                                            0xFFE3E300
                                        )
                                        else -> Color(0xFFFFFFFF)
                                    },
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.End
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(33.dp))

                        // Transliterasi
                        transliterationAyah?.let {
                            Text(
                                text = it.text,
                                fontSize = 19.sp,
                                lineHeight = 23.sp,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(
                                        0xFF00BCD4
                                    )
                                ),
                                textAlign = TextAlign.Start
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Terjemahan Indonesia
                        translationAyah?.let {
                            Text(
                                text = it.text,
                                fontSize = 15.sp,
                                lineHeight = 23.sp,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            // Tombol Play All & Stop
            Column(
                modifier = Modifier.align(Alignment.End),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Putar Semua",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Play/Pause/Resume icon
                    IconButton(
                        onClick = {
                            if (isPlayingAll) {
                                if (!isPaused) audioManager.pause() else audioManager.resume()
                            } else {
                                onPlayAll(ayahs.firstOrNull()?.numberInSurah ?: 0)
                            }
                        }
                    ) {
                        val iconRes = when {
                            isPlayingAll && !isPaused -> R.drawable.pause // <- pastikan ada di drawable
                            isPlayingAll && isPaused -> R.drawable.play
                            else -> R.drawable.play
                        }

                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = "Play All",
                            modifier = Modifier.size(36.dp), // ukuran ikon
                            tint = Color.Unspecified // biar warnanya sesuai icon asli (transparan, full color)
                        )
                    }

                    // Stop icon
                    IconButton(
                        onClick = {
                            audioManager.stop()
                            onAyahPlaying(0)
                        },
                        enabled = isPlayingAll
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.stop),
                            contentDescription = "Stop",
                            modifier = Modifier.size(36.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }

        }
    }

        }
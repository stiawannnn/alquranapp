package com.example.utsquranappq.ui.juzuI.juzzdetailscreen

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.utsquranappq.R
import com.example.utsquranappq.model.AyahEdition
import com.example.utsquranappq.utiils.parseTajweedText

@Composable
fun SurahCardOnlyText(
    surahName: String,
    ayahs: List<AyahEdition>,
    selectedQari: String?,
    audioManager: JuzAudioManager,
    currentPlayingAyah: Int?,
    onPlayAll: (Int) -> Unit // Memulai Play All dengan startIndex
) {
    var currentAyahPlaying by remember { mutableStateOf<Int?>(null) } // Pemutaran individu
    var expanded by remember { mutableStateOf(false) } // DropdownMenu
    var selectedAyahNumber by remember { mutableStateOf<Int?>(null) } // Ayat yang diklik
    val mediaPlayer = remember { MediaPlayer() } // Untuk pemutaran individu

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 11.dp, vertical = 7.dp)
    ) {
        Text(
            text = "Surah: $surahName",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (ayahs.isEmpty()) {
            Text(
                text = "Tidak ada ayat untuk ditampilkan",
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            val groupedByAyah = ayahs.groupBy { it.number }

            groupedByAyah.forEach { (_, editions) ->
                val tajweedText = editions.find { it.edition.identifier == "quran-tajweed" }?.text ?: ""
                val translation = editions.find { it.edition.identifier == "id.indonesian" }?.text ?: ""
                val transliteration = editions.find { it.edition.identifier == "en.transliteration" }?.text ?: ""
                val ayahNumber = editions.firstOrNull()?.numberInSurah ?: 0
                val audioAyah = editions.find {
                    it.audio != null && (selectedQari == null || it.edition.identifier == selectedQari)
                } ?: editions.find { it.audio != null }
                val startIndex = ayahs.indexOfFirst { it.numberInSurah == ayahNumber && it.audio != null && it.edition.identifier == selectedQari }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            selectedAyahNumber = ayahNumber
                            expanded = true
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF0E0E0D), Color(0xFF131113), Color(0xFF0C0C0C))
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.qur3),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(200.dp)
                                .alpha(0.15f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
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
                                    text = ayahNumber.toString(),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF03A9F4),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = buildAnnotatedString { append(parseTajweedText(tajweedText)) },
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 22.sp,
                                        lineHeight = 44.sp,
                                        color = if (currentPlayingAyah == ayahNumber || currentAyahPlaying == ayahNumber) Color.Yellow else Color.White,
                                        textAlign = TextAlign.End
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Latin: $transliteration",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 16.sp,
                                        color = Color(0xFF00BCD4),
                                        textAlign = TextAlign.Start
                                    )
                                )
                                Text(
                                    text = "Terjemahan: $translation",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 16.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Start
                                    )
                                )
                            }
                        }
                    }
                }

                if (selectedAyahNumber == ayahNumber) {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color(0xFF1E1E1E))
                    ) {
                        audioAyah?.audio?.let { audioUrl ->
                            // Play Audio (individu)
                            DropdownMenuItem(
                                text = { Text("Play Audio", color = Color.White) },
                                onClick = {
                                    if (currentAyahPlaying != ayahNumber) {
                                        audioManager.stop() // Hentikan Play All jika sedang berjalan
                                        mediaPlayer.stop()
                                        mediaPlayer.reset()
                                        try {
                                            mediaPlayer.setDataSource(audioUrl)
                                            mediaPlayer.prepare()
                                            mediaPlayer.start()
                                            currentAyahPlaying = ayahNumber
                                        } catch (e: Exception) {
                                            Log.e("SurahCardOnlyText", "Error playing audio: ${e.message}")
                                        }
                                        mediaPlayer.setOnCompletionListener {
                                            currentAyahPlaying = null
                                        }
                                    }
                                    expanded = false
                                }
                            )

                            // Pause Audio (individu)
                            if (currentAyahPlaying == ayahNumber && mediaPlayer.isPlaying) {
                                DropdownMenuItem(
                                    text = { Text("Pause Audio", color = Color.White) },
                                    onClick = {
                                        mediaPlayer.pause()
                                        expanded = false
                                    }
                                )
                            }

                            // Resume Audio (individu)
                            if (currentAyahPlaying == ayahNumber && !mediaPlayer.isPlaying) {
                                DropdownMenuItem(
                                    text = { Text("Resume Audio", color = Color.White) },
                                    onClick = {
                                        mediaPlayer.start()
                                        expanded = false
                                    }
                                )
                            }

                            // Stop Audio (individu)
                            if (currentAyahPlaying == ayahNumber) {
                                DropdownMenuItem(
                                    text = { Text("Stop Audio", color = Color.White) },
                                    onClick = {
                                        mediaPlayer.stop()
                                        mediaPlayer.reset()
                                        currentAyahPlaying = null
                                        expanded = false
                                    }
                                )
                            }
                        } ?: run {
                            DropdownMenuItem(
                                text = { Text("No Audio Available", color = Color.Gray) },
                                onClick = { expanded = false },
                                enabled = false
                            )
                        }

                        // Play All Audio
                        DropdownMenuItem(
                            text = { Text("Play All Audio", color = Color.White) },
                            onClick = {
                                if (!audioManager.isPlayingAll.value && selectedQari != null) {
                                    mediaPlayer.stop()
                                    mediaPlayer.reset()
                                    currentAyahPlaying = null
                                    val index = if (startIndex >= 0) startIndex else 0
                                    onPlayAll(index) // Mulai dari indeks ayat yang diklik
                                }
                                expanded = false
                            },
                            enabled = !audioManager.isPlayingAll.value && selectedQari != null
                        )

                        // Pause All Audio
                        if (audioManager.isPlayingAll.value && !audioManager.isPaused.value) {
                            DropdownMenuItem(
                                text = { Text("Pause All Audio", color = Color.White) },
                                onClick = {
                                    audioManager.pause()
                                    expanded = false
                                }
                            )
                        }

                        // Resume All Audio
                        if (audioManager.isPlayingAll.value && audioManager.isPaused.value) {
                            DropdownMenuItem(
                                text = { Text("Resume All Audio", color = Color.White) },
                                onClick = {
                                    audioManager.resume()
                                    expanded = false
                                }
                            )
                        }

                        // Stop All Audio
                        if (audioManager.isPlayingAll.value) {
                            DropdownMenuItem(
                                text = { Text("Stop All Audio", color = Color.White) },
                                onClick = {
                                    audioManager.stop()
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
package com.example.utsquranappq.ui.surahui.surahdetailscreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    // Ganti 'by' dengan akses langsung ke 'value'
    val isPlayingAll = audioManager.isPlayingAll.value
    val isPaused = audioManager.isPaused.value

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0218), contentColor = Color(0xFFAA9AAB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ayahs.forEach { ayah ->
                when (ayah.edition.identifier) {
                    "quran-tajweed" -> {
                        val annotatedText = parseTajweedText("${ayah.numberInSurah}. ${ayah.text} ")
                        Text(
                            text = annotatedText,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 27.sp,
                            color = if (currentPlayingAyah == ayah.numberInSurah) Color.Yellow else Color.White
                        )
                    }
                    "en.transliteration" -> Text(text = "Latin: ${ayah.text}", style = MaterialTheme.typography.bodyMedium)
                    "id.indonesian" -> Text(text = "Terjemahan: ${ayah.text}", style = MaterialTheme.typography.bodyMedium)
                    else -> {
                        if (ayah.audio != null && (selectedQari == null || ayah.edition.identifier == selectedQari)) {
                            AudioPlayer(
                                audioUrl = ayah.audio,
                                ayahNumber = ayah.numberInSurah,
                                qariName = ayah.edition.englishName,
                                allAyahs = allAyahs,
                                selectedQari = selectedQari,
                                onAyahPlaying = onAyahPlaying,
                                isPlayingAll = isPlayingAll
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Row(modifier = Modifier.align(Alignment.End), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (isPlayingAll) {
                            if (!isPaused) audioManager.pause() else audioManager.resume()
                        } else {
                            onPlayAll(ayahs.first().numberInSurah)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlayingAll && !isPaused) Color.Yellow else Color.Blue
                    )
                ) {
                    Text(
                        text = when {
                            isPlayingAll && !isPaused -> "Pause"
                            isPlayingAll && isPaused -> "Resume"
                            else -> "Play All"
                        },
                        color = Color.White
                    )
                }
                Button(
                    onClick = { audioManager.stop(); onAyahPlaying(0) },
                    enabled = isPlayingAll,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Stop", color = Color.White)
                }
            }
        }
    }
}
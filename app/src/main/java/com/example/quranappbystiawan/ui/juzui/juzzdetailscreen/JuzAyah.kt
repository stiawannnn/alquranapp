package com.example.quranappbystiawan.ui.juzui.juzzdetailscreen

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quranappbystiawan.R
import com.example.quranappbystiawan.model.AyahEdition
import com.example.quranappbystiawan.ui.BookmarkViewModelFactory
import com.example.quranappbystiawan.utils.parseTajweedText
import com.example.quranappbystiawan.viewmodel.BookmarkViewModel

@Composable
fun SurahCardOnlyText(
    englishTranslation: String,
    revelationType: String,
    numberOfAyahs: Int,
    surahName: String,
    ayahs: List<AyahEdition>,
    selectedQari: String?,
    audioManager: JuzAudioManager,
    currentPlayingAyah: Int?,
    onPlayAll: (Int) -> Unit // Memulai Play All dengan startIndex
) {
    val context = LocalContext.current
    var currentAyahPlaying by remember { mutableStateOf<Int?>(null) } // Pemutaran individu
    var expanded by remember { mutableStateOf(false) } // DropdownMenu
    var selectedAyahNumber by remember { mutableStateOf<Int?>(null) } // Ayat yang diklik
    val mediaPlayer = remember { MediaPlayer() } // Untuk pemutaran individu
    val juzNumber = ayahs.firstOrNull()?.juz
    val viewModel: BookmarkViewModel = viewModel(factory = BookmarkViewModelFactory(context))

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
        SurahHeaderBox(
            englishName = surahName,
            englishTranslation = englishTranslation,
            revelationType = revelationType,
            numberOfAyahs = numberOfAyahs,
            surahNumber = ayahs.firstOrNull()?.surah?.number ?: 0
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
                val surahNumber = editions.firstOrNull()?.surah?.number ?: 0
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
                            painter = painterResource(id = R.drawable.qur4),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(200.dp)
                                .alpha(0.07f)
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

                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Teks Tajweed (Rata Kanan)
                                Text(
                                    text = buildAnnotatedString { append(parseTajweedText(tajweedText)) },
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 33.sp,
                                        fontFamily = FontFamily(Font(R.font.scheherazadenew)),
                                        lineHeight = 77.sp,
                                        color = if (currentPlayingAyah == ayahNumber || currentAyahPlaying == ayahNumber) Color.Yellow else Color.White,
                                        textAlign = TextAlign.End
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.End) // Pastikan selalu rata kanan
                                )
                                Spacer(modifier = Modifier.height(11.dp))

                                // Transliterasi (Rata Kiri)
                                Text(
                                    text = transliteration,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 19.sp,
                                        lineHeight = 33.sp,
                                        color = Color(0xFF00BCD4),
                                        textAlign = TextAlign.Start
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(11.dp))

                                // Terjemahan (Rata Kiri)
                                Text(
                                    text = translation,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 16.sp,
                                        lineHeight = 33.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Start
                                    ),
                                    modifier = Modifier.fillMaxWidth()
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
                            DropdownMenuItem(
                                text = { Text("Putar Audio", color = Color.White) },
                                onClick = {
                                    if (currentAyahPlaying != ayahNumber) {
                                        audioManager.stop()
                                        mediaPlayer.stop()
                                        mediaPlayer.reset()
                                        try {
                                            mediaPlayer.setDataSource(audioUrl)
                                            mediaPlayer.prepare()
                                            mediaPlayer.start()
                                            currentAyahPlaying = ayahNumber
                                        } catch (e: Exception) {
                                            Log.e("SurahCardOnlyText", "Error memutar audio: ${e.message}")
                                        }
                                        mediaPlayer.setOnCompletionListener {
                                            currentAyahPlaying = null
                                        }
                                    }
                                    expanded = false
                                }
                            )

                            if (currentAyahPlaying == ayahNumber && mediaPlayer.isPlaying) {
                                DropdownMenuItem(
                                    text = { Text("Jeda Audio", color = Color.White) },
                                    onClick = {
                                        mediaPlayer.pause()
                                        expanded = false
                                    }
                                )
                            }

                            if (currentAyahPlaying == ayahNumber && !mediaPlayer.isPlaying) {
                                DropdownMenuItem(
                                    text = { Text("Lanjutkan Audio", color = Color.White) },
                                    onClick = {
                                        mediaPlayer.start()
                                        expanded = false
                                    }
                                )
                            }

                            if (currentAyahPlaying == ayahNumber) {
                                DropdownMenuItem(
                                    text = { Text("Hentikan Audio", color = Color.White) },
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
                                text = { Text("Tidak Ada Audio", color = Color.Gray) },
                                onClick = { expanded = false },
                                enabled = false
                            )
                        }

                        DropdownMenuItem(
                            text = { Text("Putar Semua Audio", color = Color.White) },
                            onClick = {
                                if (!audioManager.isPlayingAll.value && selectedQari != null) {
                                    mediaPlayer.stop()
                                    mediaPlayer.reset()
                                    currentAyahPlaying = null
                                    val index = if (startIndex >= 0) startIndex else 0
                                    onPlayAll(index)
                                }
                                expanded = false
                            },
                            enabled = !audioManager.isPlayingAll.value && selectedQari != null
                        )

                        if (audioManager.isPlayingAll.value && !audioManager.isPaused.value) {
                            DropdownMenuItem(
                                text = { Text("Jeda Semua Audio", color = Color.White) },
                                onClick = {
                                    audioManager.pause()
                                    expanded = false
                                }
                            )
                        }

                        if (audioManager.isPlayingAll.value && audioManager.isPaused.value) {
                            DropdownMenuItem(
                                text = { Text("Lanjutkan Semua Audio", color = Color.White) },
                                onClick = {
                                    audioManager.resume()
                                    expanded = false
                                }
                            )
                        }

                        if (audioManager.isPlayingAll.value) {
                            DropdownMenuItem(
                                text = { Text("Hentikan Semua Audio", color = Color.White) },
                                onClick = {
                                    audioManager.stop()
                                    expanded = false
                                }
                            )
                        }

                        // Opsi Bookmark
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (viewModel.isBookmarked(surahNumber, ayahNumber))
                                        "Hapus Bookmark"
                                    else "Tambah Bookmark",
                                    color = Color.White
                                )
                            },
                            onClick = {
                                if (viewModel.isBookmarked(surahNumber, ayahNumber)) {
                                    viewModel.removeBookmark(surahNumber, ayahNumber)
                                } else {
                                    viewModel.addBookmark(surahNumber, ayahNumber, surahName,juzNumber)
                                }
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

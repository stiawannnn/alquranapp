package com.example.utsquranappq.ui

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.utsquranappq.R
import com.example.utsquranappq.model.AyahEdition
import com.example.utsquranappq.utiils.parseTajweedText
import com.example.utsquranappq.viewmodel.JuzViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuzDetailScreen(
    juzNumber: Int?,
    navController: NavController,
    viewModel: JuzViewModel = viewModel()
) {
    if (juzNumber == null || juzNumber !in 1..30) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Invalid Juz Number")
        }
        return
    }

    val juzDetail by viewModel.juzDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var showQariDialog by remember { mutableStateOf(false) }
    var selectedQari by remember { mutableStateOf<String?>(null) }
    val mediaPlayer = remember { MediaPlayer() }
    var isPlayingAll by remember { mutableStateOf(false) }
    var currentPlayingAyah by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    LaunchedEffect(juzNumber) {
        viewModel.fetchJuzDetail(juzNumber)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Juz $juzNumber") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.arrowback), contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { showQariDialog = true }) {
                        Icon(painter = painterResource(id = R.drawable.qur6), contentDescription = "Menu")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                    }
                }
                juzDetail.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No data available for Juz $juzNumber")
                    }
                }
                else -> {
                    val grouped = juzDetail.groupBy { it.surah.number }

                    LazyColumn {
                        items(grouped.entries.toList()) { (surahNumber, ayahs) ->
                            val surahName = ayahs.firstOrNull()?.surah?.englishName ?: "Surah $surahNumber"

                            SurahCardOnlyText(
                                surahName = surahName,
                                ayahs = ayahs,
                                selectedQari = selectedQari,
                                mediaPlayer = mediaPlayer,
                                isPlayingAll = isPlayingAll,
                                currentPlayingAyah = currentPlayingAyah,
                                onTogglePlayAll = {
                                    if (!isPlayingAll && selectedQari != null) {
                                        isPlayingAll = true
                                        coroutineScope.launch {
                                            playAllAudio(
                                                ayahs = ayahs,
                                                selectedQari = selectedQari,
                                                mediaPlayer = mediaPlayer,
                                                onAyahPlaying = { ayahNumber -> currentPlayingAyah = ayahNumber },
                                                onFinished = {
                                                    isPlayingAll = false
                                                    currentPlayingAyah = null
                                                }
                                            )
                                        }
                                    } else {
                                        isPlayingAll = false
                                        mediaPlayer.stop()
                                        mediaPlayer.reset()
                                        currentPlayingAyah = null
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showQariDialog) {
            VoiceSelectionDialog(
                onDismiss = { showQariDialog = false },
                surahDetail = juzDetail,
                onQariSelected = { qari ->
                    selectedQari = qari
                    showQariDialog = false
                }
            )
        }
    }
}

@Composable
fun SurahCardOnlyText(
    surahName: String,
    ayahs: List<AyahEdition>,
    selectedQari: String?,
    mediaPlayer: MediaPlayer,
    isPlayingAll: Boolean,
    currentPlayingAyah: Int?,
    onTogglePlayAll: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F0218),
            contentColor = Color(0xFFAA9AAB)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Surah: $surahName",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                )
                TextButton(
                    onClick = {
                        if (selectedQari == null) {
                            // Anda mungkin ingin menambahkan toast atau dialog untuk memilih qari terlebih dahulu
                        } else {
                            onTogglePlayAll()
                        }
                    },
                    enabled = selectedQari != null
                ) {
                    Text(
                        if (isPlayingAll) "Stop All" else "Play All",
                        color = if (selectedQari != null) Color.Green else Color.Gray
                    )
                }
            }

            val groupedByAyah = ayahs.groupBy { it.number }

            groupedByAyah.forEach { (_, editions) ->
                val tajweedText = editions.find { it.edition.identifier == "quran-tajweed" }?.text ?: ""
                val translation = editions.find { it.edition.identifier == "id.indonesian" }?.text ?: ""
                val transliteration = editions.find { it.edition.identifier == "en.transliteration" }?.text ?: ""
                val ayahNumber = editions.first().numberInSurah

                Text(
                    text = buildAnnotatedString { append(parseTajweedText(tajweedText)) },
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp),
                    color = if (currentPlayingAyah == ayahNumber) Color.Yellow else Color.Unspecified
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Latin: $transliteration", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray))
                Text(text = "Terjemahan: $translation", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray))
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

suspend fun playAllAudio(
    ayahs: List<AyahEdition>,
    selectedQari: String?,
    mediaPlayer: MediaPlayer,
    onAyahPlaying: (Int) -> Unit,
    onFinished: () -> Unit
) {
    try {
        for (ayah in ayahs) {
            // Asumsi bahwa AyahEdition memiliki field audio yang berisi URL untuk qari tertentu
            val audioEdition = ayah.edition.identifier.contains(selectedQari ?: "")
            val audioUrl = ayah.audio.takeIf { audioEdition } // Ganti dengan field audio yang benar dari model Anda

            if (!audioUrl.isNullOrEmpty()) {
                try {
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(audioUrl) // Gunakan URL audio yang sesuai
                    mediaPlayer.prepare()
                    onAyahPlaying(ayah.numberInSurah)
                    mediaPlayer.start()

                    // Tunggu sampai audio selesai
                    while (mediaPlayer.isPlaying) {
                        delay(100)
                    }
                } catch (e: Exception) {
                    Log.e("AudioPlayer", "Error playing ayah ${ayah.numberInSurah}: ${e.message}")
                    continue // Lanjut ke ayah berikutnya jika ada error
                }
            }
        }
    } catch (e: Exception) {
        Log.e("AudioPlayer", "Error in playAllAudio: ${e.message}")
    } finally {
        mediaPlayer.reset()
        onFinished()
    }
}
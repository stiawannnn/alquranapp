package com.example.utsquranappq.ui

import android.media.MediaPlayer
import android.util.Log
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.utsquranappq.R
import com.example.utsquranappq.model.AyahEdition
import com.example.utsquranappq.viewmodel.SurahDetailViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.utsquranappq.model.Surah
import com.example.utsquranappq.utiils.getTranslation
import com.example.utsquranappq.utiils.parseTajweedText
import kotlinx.coroutines.delay
@Composable
fun SurahDetailScreen(
    surahNumber: Int?,
    navController: NavController,
    viewModel: SurahDetailViewModel = viewModel(),
    surahViewModel: com.example.quranapp.viewmodel.SurahViewModel = viewModel()
) {
    Log.d("SurahDetailScreen", "Screen loaded with surahNumber: $surahNumber")

    if (surahNumber == null || surahNumber <= 0) {
        Log.e("SurahDetailScreen", "Invalid surahNumber, displaying empty UI")
        Box(modifier = Modifier.fillMaxSize()) { /* UI kosong */ }
        return
    }

    val surahDetail by viewModel.surahDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.error.collectAsState()
    val surahList by surahViewModel.surahList.collectAsState()
    val currentSurah = surahList.find { it.number == surahNumber }

    LaunchedEffect(surahNumber) {
        Log.d("SurahDetailScreen", "Fetching data for surahNumber: $surahNumber")
        viewModel.fetchSurahDetail(surahNumber)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                Log.d("SurahDetailScreen", "Loading data...")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Log.e("SurahDetailScreen", "Error encountered: $errorMessage")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "Terjadi kesalahan",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            surahDetail.isEmpty() -> {
                Log.w("SurahDetailScreen", "No ayahs found for this Surah")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada data ayat", style = MaterialTheme.typography.bodyLarge)
                }
            }
            else -> {
                Log.d("SurahDetailScreen", "Displaying ayahs")
                LazyColumn {
                    item {
                        detailldariayat(currentSurah = currentSurah)
                    }
                    items(surahDetail.groupBy { it.numberInSurah }.keys.toList()) { numberInSurah ->
                        val ayahs = surahDetail.filter { it.numberInSurah == numberInSurah }
                        AyahCard(ayahs)
                    }
                }
            }
        }
    }
}

// Fungsi detailldariayat tetap sama seperti sebelumnya
@Composable
fun detailldariayat(currentSurah: Surah?) {
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
                    colors = listOf(Color(0xFF673AB7), Color(0xFFC49CC9), Color(0xFFC804EA))
                ),
                shape = RoundedCornerShape(23.dp)
            )
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.qur3),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(244.dp)
                .alpha(0.2f)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            currentSurah?.let {
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

            Image(
                painter = painterResource(id = R.drawable.bismillah),
                contentDescription = "Bismillah",
                modifier = Modifier.size(223.dp)
            )
        }
    }
}
@Composable
fun AyahCard(ayahs: List<AyahEdition>) {
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
            ayahs.forEach { ayah ->
                when (ayah.edition.identifier) {
                    "quran-tajweed" -> {
                        Log.d("AyahText", "Raw text: ${ayah.text}")
                        val annotatedText = parseTajweedText("${ayah.numberInSurah}. ${ayah.text}")
                        Text(
                            text = annotatedText,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 27.sp,
                            color = Color.White
                        )
                    }
                    "en.transliteration" -> {
                        Text(
                            text = "Latin: ${ayah.text}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    "id.indonesian" -> {
                        Text(
                            text = "Terjemahan: ${ayah.text}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    "ar.alafasy" -> {
                        // Tambahkan kontrol audio untuk edisi ar.alafasy
                        ayah.audio?.let { audioUrl ->
                            AudioPlayer(audioUrl = audioUrl, ayahNumber = ayah.numberInSurah)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun AudioPlayer(audioUrl: String, ayahNumber: Int) {
    val mediaPlayer = remember { MediaPlayer() }
    var isPlaying by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }

    // Mengatur MediaPlayer saat pertama kali dibuat
    LaunchedEffect(audioUrl) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(audioUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                isPrepared = true
            }
            mediaPlayer.setOnCompletionListener {
                isPlaying = false
                mediaPlayer.seekTo(0) // Kembali ke awal setelah selesai
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error preparing audio: ${e.message}")
        }
    }

    // Membersihkan MediaPlayer saat Composable dihapus
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Audio Ayat $ayahNumber",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                if (isPrepared) {
                    if (isPlaying) {
                        mediaPlayer.pause()
                        isPlaying = false
                    } else {
                        mediaPlayer.start()
                        isPlaying = true
                    }
                }
            },
            enabled = isPrepared,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPlaying) Color.Red else Color.Green
            )
        ) {
            Text(
                text = if (isPlaying) "Pause" else "Play",
                color = Color.White
            )
        }
    }
}
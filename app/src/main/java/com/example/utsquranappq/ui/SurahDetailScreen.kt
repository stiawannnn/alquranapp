package com.example.utsquranappq.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.utsquranappq.R
import com.example.utsquranappq.model.AyahEdition
import com.example.utsquranappq.viewmodel.SurahDetailViewModel

@Composable
fun SurahDetailScreen(
    surahNumber: Int?,
    navController: NavController,
    viewModel: SurahDetailViewModel = viewModel()
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
                    items(surahDetail.groupBy { it.numberInSurah }.keys.toList()) { numberInSurah ->
                        val ayahs = surahDetail.filter { it.numberInSurah == numberInSurah }
                        AyahCard(ayahs)
                    }
                }
            }
        }
    }
}

@Composable
fun AyahCard(ayahs: List<AyahEdition>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ayahs.forEach { ayah ->
                when (ayah.edition.identifier) {
                    "quran-uthmani" -> {
                        Text(
                            text = "${ayah.numberInSurah}. ${ayah.text}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontFamily(Font(R.font.hafs))
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
                            text = "Terjemahan: ${ayah.text} ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

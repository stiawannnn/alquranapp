package com.example.utsquranappq.ui

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.utsquranappq.model.Surah
import com.example.utsquranappq.utiils.getTranslation
import com.example.utsquranappq.utiils.parseTajweedText
import kotlinx.coroutines.delay
@OptIn(ExperimentalMaterial3Api::class)
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

    // State untuk menu dan scroll
    var menuExpanded by remember { mutableStateOf(false) }
    var showVoiceDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    var targetAyahNumber by remember { mutableStateOf<Int?>(null) } // State untuk nomor ayat yang dicari

    LaunchedEffect(surahNumber) {
        Log.d("SurahDetailScreen", "Fetching data for surahNumber: $surahNumber")
        viewModel.fetchSurahDetail(surahNumber)
    }

    // Efek untuk scroll ke ayat yang dicari
    LaunchedEffect(targetAyahNumber) {
        targetAyahNumber?.let { ayahNumber ->
            val ayahKeys = surahDetail.groupBy { it.numberInSurah }.keys.toList()
            val index = ayahKeys.indexOf(ayahNumber)
            if (index != -1) {
                listState.animateScrollToItem(index + 1) // +1 karena ada header
                Log.d("SurahDetailScreen", "Scrolled to ayah: $ayahNumber at index: ${index + 1}")
            } else {
                Log.w("SurahDetailScreen", "Ayah number $ayahNumber not found")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Surah ${currentSurah?.name ?: ""}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrowback), // Ganti dengan ikon Anda
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.qur6), // Ganti dengan ikon Anda
                            contentDescription = "Menu"
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Pilih Suara") },
                            onClick = {
                                showVoiceDialog = true
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Search Nomor Ayat") },
                            onClick = {
                                showSearchDialog = true
                                menuExpanded = false
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
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
                    LazyColumn(
                        state = listState
                    ) {
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

        // Dialog Pilih Suara
        if (showVoiceDialog) {
            VoiceSelectionDialog(
                onDismiss = { showVoiceDialog = false },
                surahDetail = surahDetail
            )
        }

        // Dialog Search Nomor Ayat
        if (showSearchDialog) {
            SearchAyahDialog(
                onDismiss = { showSearchDialog = false },
                onSearch = { ayahNumber ->
                    targetAyahNumber = ayahNumber.toIntOrNull() // Set nomor ayat untuk trigger scroll
                }
            )
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
// Dialog untuk memilih suara
@Composable
fun VoiceSelectionDialog(
    onDismiss: () -> Unit,
    surahDetail: List<AyahEdition>
) {
    val qariList = surahDetail.filter { it.edition.language == "ar" && it.audio != null }
        .map { it.edition.englishName }.distinct()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Suara Qari") },
        text = {
            LazyColumn {
                items(qariList) { qari ->
                    Text(
                        text = qari,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Log.d("VoiceSelection", "Selected qari: $qari")
                                // Logika untuk memilih qari tertentu (misalnya filter UI)
                                onDismiss()
                            }
                            .padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

// Dialog untuk mencari nomor ayat
@Composable
fun SearchAyahDialog(
    onDismiss: () -> Unit,
    onSearch: (String) -> Unit
) {
    var ayahNumber by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cari Nomor Ayat") },
        text = {
            TextField(
                value = ayahNumber,
                onValueChange = { ayahNumber = it },
                label = { Text("Masukkan nomor ayat") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onSearch(ayahNumber)
                onDismiss()
            }) {
                Text("Cari")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
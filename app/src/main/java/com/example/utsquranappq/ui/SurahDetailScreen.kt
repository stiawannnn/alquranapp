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
import kotlinx.coroutines.launch

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
    val selectedQari by viewModel.selectedQari.collectAsState()
    val surahList by surahViewModel.surahList.collectAsState()
    val currentSurah = surahList.find { it.number == surahNumber }

    var menuExpanded by remember { mutableStateOf(false) }
    var showVoiceDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    var targetAyahNumber by remember { mutableStateOf<Int?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // State untuk mengontrol Play All
    var isPlayingAll by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer() }

    // Fungsi untuk memulai Play All
    fun startPlayAll(ayahs: List<AyahEdition>, currentAyahNumber: Int) {
        if (!isPlayingAll) {
            coroutineScope.launch {
                isPlayingAll = true
                isPaused = false
                playAllAudio(ayahs, selectedQari, surahDetail, currentAyahNumber, mediaPlayer, { isPlayingAll }, { isPaused }) {
                    isPlayingAll = false
                    isPaused = false
                }
            }
        }
    }

    LaunchedEffect(surahNumber) {
        Log.d("SurahDetailScreen", "Fetching data for surahNumber: $surahNumber")
        viewModel.fetchSurahDetail(surahNumber)
    }

    LaunchedEffect(targetAyahNumber) {
        targetAyahNumber?.let { ayahNumber ->
            val ayahKeys = surahDetail.groupBy { it.numberInSurah }.keys.toList()
            val index = ayahKeys.indexOf(ayahNumber)
            if (index != -1) {
                listState.scrollToItem(index + 1)
                Log.d("SurahDetailScreen", "Scrolled to ayah: $ayahNumber at index: ${index + 1}")
            } else {
                Log.w("SurahDetailScreen", "Ayah number $ayahNumber not found")
                snackbarHostState.showSnackbar("Ayat nomor $ayahNumber tidak ditemukan")
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
                            painter = painterResource(id = R.drawable.arrowback),
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.qur6),
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                            AyahCard(
                                ayahs = ayahs,
                                selectedQari = selectedQari,
                                allAyahs = surahDetail,
                                isPlayingAll = isPlayingAll,
                                isPaused = isPaused,
                                onPlayAll = { startPlayAll(ayahs, numberInSurah) },
                                onPauseResume = {
                                    if (isPlayingAll && !isPaused) {
                                        mediaPlayer.pause()
                                        isPaused = true
                                    } else if (isPlayingAll && isPaused) {
                                        mediaPlayer.start()
                                        isPaused = false
                                    }
                                },
                                onStopAll = {
                                    mediaPlayer.stop()
                                    mediaPlayer.reset()
                                    isPlayingAll = false
                                    isPaused = false
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showVoiceDialog) {
            VoiceSelectionDialog(
                onDismiss = { showVoiceDialog = false },
                surahDetail = surahDetail,
                onQariSelected = { qariIdentifier ->
                    viewModel.selectQari(qariIdentifier)
                }
            )
        }

        if (showSearchDialog) {
            SearchAyahDialog(
                onDismiss = { showSearchDialog = false },
                onSearch = { ayahNumber ->
                    targetAyahNumber = ayahNumber.toIntOrNull()
                }
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
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
fun AyahCard(
    ayahs: List<AyahEdition>,
    selectedQari: String?,
    allAyahs: List<AyahEdition>,
    isPlayingAll: Boolean,
    isPaused: Boolean,
    onPlayAll: () -> Unit,
    onPauseResume: () -> Unit,
    onStopAll: () -> Unit
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
                    else -> {
                        if (ayah.audio != null) {
                            if (selectedQari == null) {
                                AudioPlayer(
                                    audioUrl = ayah.audio,
                                    ayahNumber = ayah.numberInSurah,
                                    qariName = ayah.edition.englishName,
                                    allAyahs = allAyahs,
                                    currentAyahNumber = ayah.numberInSurah,
                                    selectedQari = selectedQari
                                )
                            } else if (ayah.edition.identifier == selectedQari) {
                                AudioPlayer(
                                    audioUrl = ayah.audio,
                                    ayahNumber = ayah.numberInSurah,
                                    qariName = ayah.edition.englishName,
                                    allAyahs = allAyahs,
                                    currentAyahNumber = ayah.numberInSurah,
                                    selectedQari = selectedQari
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = if (isPlayingAll) onPauseResume else onPlayAll,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when {
                            isPlayingAll && !isPaused -> Color.Yellow // Pause
                            else -> Color.Blue // Play/Resume
                        }
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
                    onClick = onStopAll,
                    enabled = isPlayingAll,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Stop", color = Color.White)
                }
            }
        }
    }
}
@Composable
fun AudioPlayer(
    audioUrl: String,
    ayahNumber: Int,
    qariName: String,
    allAyahs: List<AyahEdition>,
    currentAyahNumber: Int,
    selectedQari: String? // Tambahkan selectedQari sebagai parameter
) {
    val mediaPlayer = remember { MediaPlayer() }
    var isPlaying by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }

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
                mediaPlayer.seekTo(0)
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error preparing audio: ${e.message}")
        }
    }

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
            text = "Audio Ayat $ayahNumber ($qariName)",
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
    surahDetail: List<AyahEdition>,
    onQariSelected: (String?) -> Unit // Callback untuk qari yang dipilih
) {
    val qariList = surahDetail.filter { it.edition.language == "ar" && it.audio != null }
        .map { it.edition }.distinctBy { it.identifier }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Suara Qari") },
        text = {
            LazyColumn {
                // Tambahkan opsi "Semua Qari"
                item {
                    Text(
                        text = "Semua Qari",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onQariSelected(null) // Null untuk menampilkan semua qari
                                onDismiss()
                            }
                            .padding(8.dp)
                    )
                }
                items(qariList) { qari ->
                    Text(
                        text = qari.englishName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onQariSelected(qari.identifier) // Kirim identifier qari
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
suspend fun playAllAudio(
    ayahs: List<AyahEdition>,
    selectedQari: String?,
    allAyahs: List<AyahEdition>,
    currentAyahNumber: Int,
    mediaPlayer: MediaPlayer,
    getIsPlayingAll: () -> Boolean,
    getIsPaused: () -> Boolean,
    onFinished: () -> Unit
) {
    try {
        val ayahNumbers = allAyahs.groupBy { it.numberInSurah }.keys.filter { it >= currentAyahNumber }.sorted()
        for (ayahNumber in ayahNumbers) {
            if (!getIsPlayingAll()) break
            val currentAyahs = allAyahs.filter { it.numberInSurah == ayahNumber }
            val audioAyahs = currentAyahs.filter { ayah ->
                ayah.audio != null && (selectedQari == null || ayah.edition.identifier == selectedQari)
            }
            for (ayah in audioAyahs) {
                if (!getIsPlayingAll()) break
                mediaPlayer.reset()
                mediaPlayer.setDataSource(ayah.audio!!)
                mediaPlayer.prepare()
                mediaPlayer.start()
                Log.d("PlayAllAudio", "Playing audio for ayah $ayahNumber (${ayah.edition.englishName})")

                var pausedPosition = 0 // Simpan posisi saat dijeda
                while (mediaPlayer.isPlaying || getIsPaused()) {
                    if (getIsPaused()) {
                        pausedPosition = mediaPlayer.currentPosition // Simpan posisi saat pause
                        mediaPlayer.pause()
                        while (getIsPaused() && getIsPlayingAll()) {
                            delay(100) // Tunggu sampai resume atau stop
                        }
                        if (!getIsPlayingAll()) break
                        mediaPlayer.seekTo(pausedPosition) // Lanjutkan dari posisi terakhir
                        mediaPlayer.start()
                    }
                    delay(100)
                }
            }
        }
    } catch (e: Exception) {
        Log.e("PlayAllAudio", "Error playing all audio: ${e.message}")
    } finally {
        if (!getIsPaused()) {
            mediaPlayer.reset()
        }
        onFinished()
    }
}
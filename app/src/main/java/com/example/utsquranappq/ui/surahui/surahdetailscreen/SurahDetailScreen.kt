package com.example.utsquranappq.ui.surahui.surahdetailscreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quranapp.viewmodel.SurahViewModel
import com.example.utsquranappq.R
import com.example.utsquranappq.viewmodel.SurahDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahDetailScreen(
    surahNumber: Int?,
    navController: NavController,
    viewModel: SurahDetailViewModel = viewModel(),
    surahViewModel: SurahViewModel = viewModel()
) {
    if (surahNumber == null || surahNumber <= 0) {
        Log.e("SurahDetailScreen", "Invalid surahNumber")
        Box(modifier = Modifier.fillMaxSize())
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
    var currentPlayingAyah by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val audioManager = remember { AudioManager() }

    LaunchedEffect(surahNumber) {
        viewModel.fetchSurahDetail(surahNumber)
    }

    LaunchedEffect(targetAyahNumber) {
        targetAyahNumber?.let { ayahNumber ->
            val ayahKeys = surahDetail.groupBy { it.numberInSurah }.keys.toList()
            val index = ayahKeys.indexOf(ayahNumber)
            if (index != -1) {
                listState.scrollToItem(index + 1)
            } else {
                snackbarHostState.showSnackbar("Ayat nomor $ayahNumber tidak ditemukan")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Surah ${currentSurah?.englishName ?: ""}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.arrowback), contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(painter = painterResource(id = R.drawable.qur6), contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Pilih Suara") },
                            onClick = { showVoiceDialog = true; menuExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Search Nomor Ayat") },
                            onClick = { showSearchDialog = true; menuExpanded = false }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF020613),
                    titleContentColor = Color(0xFFECE7E7),
                    actionIconContentColor = Color(0xFF673AB7),
                    navigationIconContentColor = Color(0xFF9C27B0)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF020613), // Biru gelap
                            Color(0xFF000000), // Biru medium
                            Color(0xFF06040C)  // Hijau muda keabu-abuan
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            when {
                isLoading -> LoadingView()
                errorMessage != null -> ErrorView(errorMessage)
                surahDetail.isEmpty() -> EmptyView()
                else -> {
                    LazyColumn(state = listState) {
                        item {
                            SurahHeader(
                                currentSurah = currentSurah,
                                surahDetail = surahDetail
                            )
                        }
                        items(surahDetail.groupBy { it.numberInSurah }.keys.toList()) { numberInSurah ->
                            val ayahs = surahDetail.filter { it.numberInSurah == numberInSurah }
                            AyahCard(
                                ayahs = ayahs,
                                selectedQari = selectedQari,
                                allAyahs = surahDetail,
                                currentPlayingAyah = currentPlayingAyah,
                                audioManager = audioManager,
                                onAyahPlaying = { ayahNumber -> currentPlayingAyah = ayahNumber },
                                onPlayAll = { startIndex ->
                                    coroutineScope.launch {
                                        audioManager.playAll(
                                            ayahs = ayahs,
                                            selectedQari = selectedQari,
                                            allAyahs = surahDetail,
                                            startIndex = startIndex,
                                            onAyahPlaying = { currentPlayingAyah = it },
                                            onFinished = { currentPlayingAyah = null }
                                        )
                                    }
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
                onQariSelected = viewModel::selectQari
            )
        }

        if (showSearchDialog) {
            SearchAyahDialog(
                onDismiss = { showSearchDialog = false },
                onSearch = { targetAyahNumber = it.toIntOrNull() }
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose { audioManager.release() }
    }
}

@Composable
fun LoadingView() = Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    CircularProgressIndicator()
}

@Composable
fun ErrorView(errorMessage: String?) = Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Text(
        text = errorMessage ?: "Terjadi kesalahan",
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun EmptyView() = Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Text("Tidak ada data ayat", style = MaterialTheme.typography.bodyLarge)
}

package com.example.utsquranappq.ui.juzuI.juzzdetailscreen

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.utsquranappq.R
import com.example.utsquranappq.model.AyahEdition
import com.example.utsquranappq.ui.surahui.surahdetailscreen.VoiceSelectionDialog
import com.example.utsquranappq.viewmodel.JuzViewModel
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
    var selectedQari by remember { mutableStateOf<String?>("ar.alafasy") }
    val audioManager = remember { JuzAudioManager() }
    var currentPlayingAyah by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    DisposableEffect(Unit) {
        onDispose {
            audioManager.release()
        }
    }

    LaunchedEffect(juzNumber) {
        viewModel.fetchJuzDetail(juzNumber, reset = true)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()?.index ?: 0
                val totalItems = juzDetail.groupBy { it.surah.number }.size
                if (lastVisibleItem >= totalItems - 3 && viewModel.hasMoreAyahs() && !isLoading) {
                    viewModel.fetchJuzDetail(juzNumber)
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Juz $juzNumber") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrowback),
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showQariDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.qur6),
                            contentDescription = "Menu"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF100F0F),
                    titleContentColor = Color(0xFFFDFAFA),
                    navigationIconContentColor = Color(0xFF472694),
                    actionIconContentColor = Color(0xFF03A9F4)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0E0E0E),
                            Color(0xFF090808),
                            Color(0xFF0E0E0E)
                        )
                    )
                )
        ) {
            when {
                isLoading && juzDetail.isEmpty() -> {
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

                    LazyColumn(state = listState) {
                        items(grouped.entries.toList()) { (surahNumber, ayahs) ->
                            val surah = ayahs.firstOrNull()?.surah
                            val surahName = surah?.englishName ?: "Surah $surahNumber"
                            val englishTranslation = surah?.englishNameTranslation ?: "Unknown Translation"
                            val revelationType = surah?.revelationType ?: "Unknown Type"
                            val numberOfAyahs = surah?.numberOfAyahs ?: 0 // Ambil jumlah ayat

                            SurahCardOnlyText(
                                englishTranslation = englishTranslation,
                                revelationType = revelationType,
                                numberOfAyahs = numberOfAyahs,
                                surahName = surahName,
                                ayahs = ayahs,
                                selectedQari = selectedQari,
                                audioManager = audioManager,
                                currentPlayingAyah = currentPlayingAyah,
                                onPlayAll = { startIndex ->
                                    coroutineScope.launch {
                                        audioManager.playAll(
                                            ayahs = ayahs,
                                            selectedQari = selectedQari,
                                            allAyahs = juzDetail,
                                            startIndex = startIndex,
                                            onAyahPlaying = { currentPlayingAyah = it },
                                            onFinished = { currentPlayingAyah = null }
                                        )
                                    }
                                }
                            )
                        }
                        if (isLoading) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
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
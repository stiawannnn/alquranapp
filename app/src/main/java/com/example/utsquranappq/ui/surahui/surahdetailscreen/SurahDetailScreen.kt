package com.example.utsquranappq.ui.surahui.surahdetailscreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quranapp.viewmodel.SurahViewModel
import com.example.utsquranappq.R
import com.example.utsquranappq.ui.saveLastSeen
import com.example.utsquranappq.viewmodel.SurahDetailViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
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

    val context = LocalContext.current
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
    val snackbarHostState = remember { SnackbarHostState() }
    var currentPlayingAyah by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val audioManager = remember { AudioManager() }

    // Ambil targetAyahNumber dari argumen navigasi
    val targetAyahNumber = navController.currentBackStackEntry?.arguments?.getString("ayah")?.toIntOrNull()
    Log.d("SurahDetailScreen", "Target Ayah Number: $targetAyahNumber")

    // Fetch data
    LaunchedEffect(surahNumber) {
        Log.d("SurahDetailScreen", "Fetching Surah $surahNumber")
        viewModel.fetchSurahDetail(surahNumber, reset = true)
    }

    // Scroll ke ayat target setelah data dimuat
    LaunchedEffect(surahDetail, targetAyahNumber) {
        if (targetAyahNumber != null && surahDetail.isNotEmpty()) {
            val ayahKeys = surahDetail.groupBy { it.numberInSurah }.keys.toList()
            val index = ayahKeys.indexOf(targetAyahNumber)
            if (index != -1) {
                snapshotFlow { listState.isScrollInProgress }
                    .filter { !it } // Tunggu scroll selesai
                    .first()
                listState.scrollToItem(index + 1)
                Log.d("SurahDetailScreen", "Scrolled to Ayah: $targetAyahNumber, Index: $index")
            }
        }
    }
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .debounce(500)
            .collect { index ->
                if (index >= 0 && surahDetail.isNotEmpty()) {
                    val ayah = surahDetail.groupBy { it.numberInSurah }.keys.toList().getOrNull(index)
                    ayah?.let {
                        saveLastSeen(context, surah = surahNumber, ayah = it)
                    }
                }
            }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()?.index ?: 0
                val totalItems = surahDetail.groupBy { it.numberInSurah }.size
                if (lastVisibleItem >= totalItems - 3 && viewModel.hasMoreAyahs() && !isLoading) {
                    viewModel.fetchSurahDetail(surahNumber)
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
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color(0xFF1E1E1E))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Pilih Suara", color = Color.White) },
                            onClick = { showVoiceDialog = true; menuExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Cari Nomor Ayat", color = Color.White) },
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
                        colors = listOf(Color(0xFF020613), Color(0xFF000000), Color(0xFF06040C))
                    )
                )
                .padding(paddingValues)
        ) {
            when {
                isLoading && surahDetail.isEmpty() -> LoadingView()
                errorMessage != null -> ErrorView(errorMessage)
                surahDetail.isEmpty() -> EmptyView()
                else -> {
                    LazyColumn(state = listState) {
                        item {
                            SurahHeader(currentSurah = currentSurah, surahDetail = surahDetail)
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
                        if (isLoading) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
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
                surahNumber = surahNumber,
                viewModel = viewModel,
                onDismiss = { showSearchDialog = false },
                onSearch = { targetAyahNumber ->
                    coroutineScope.launch {
                        viewModel.fetchSurahDetail(surahNumber, reset = true, targetAyahNumber = targetAyahNumber.toIntOrNull())
                        while (viewModel.isLoading.value) {
                            delay(100)
                        }
                        val ayahKeys = surahDetail.groupBy { it.numberInSurah }.keys.toList()
                        val index = ayahKeys.indexOf(targetAyahNumber.toIntOrNull())
                        if (index != -1) {
                            listState.scrollToItem(index + 1)
                            Log.d("SurahSearch", "Scrolled to Ayah: $targetAyahNumber, Index: $index")
                        } else {
                            snackbarHostState.showSnackbar("Ayat nomor $targetAyahNumber tidak ditemukan")
                            Log.e("SurahSearch", "Ayah $targetAyahNumber not found in Surah $surahNumber")
                        }
                        showSearchDialog = false
                    }
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAyahDialog(
    surahNumber: Int,
    viewModel: SurahDetailViewModel,
    onDismiss: () -> Unit,
    onSearch: (String) -> Unit
) {
    var ayahNumber by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E1E1E),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Cari Nomor Ayat", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = ayahNumber,
                    onValueChange = { ayahNumber = it },
                    label = { Text("Nomor Ayat", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF03A9F4),
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal", color = Color.White)
                    }
                    TextButton(
                        onClick = { if (ayahNumber.isNotEmpty()) onSearch(ayahNumber) },
                        enabled = ayahNumber.isNotEmpty()
                    ) {
                        Text("Cari", color = if (ayahNumber.isNotEmpty()) Color.White else Color.Gray)
                    }
                }
            }
        }
    }
}
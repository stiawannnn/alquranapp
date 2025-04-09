package com.example.utsquranappq.ui.juzuI.juzzdetailscreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import com.example.utsquranappq.R
import com.example.utsquranappq.repository.QuranRepository
import com.example.utsquranappq.ui.saveLastSeen
import com.example.utsquranappq.ui.surahui.surahdetailscreen.VoiceSelectionDialog
import com.example.utsquranappq.viewmodel.JuzViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
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

    val context = LocalContext.current
    val juzDetail by viewModel.juzDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var showQariDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var selectedQari by remember { mutableStateOf<String?>("ar.alafasy") }
    val audioManager = remember { JuzAudioManager() }
    var currentPlayingAyah by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(juzNumber) {
        viewModel.fetchJuzDetail(juzNumber, reset = true)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.layoutInfo.visibleItemsInfo }
            .debounce(500)
            .collect { (firstVisibleIndex, visibleItems) ->
                if (firstVisibleIndex >= 0 && juzDetail.isNotEmpty()) {
                    val grouped = juzDetail.groupBy { it.surah.number }
                    val surahEntries = grouped.entries.toList()
                    val lastVisibleIndex = visibleItems.lastOrNull()?.index ?: firstVisibleIndex
                    val entry = surahEntries.getOrNull(lastVisibleIndex)
                    if (entry != null) {
                        val surahNumber = entry.key
                        val lastAyah = entry.value.lastOrNull()?.numberInSurah
                        if (lastAyah != null) {
                            saveLastSeen(context, juz = juzNumber, juzSurah = surahNumber, juzAyah = lastAyah)
                            Log.d("JuzScroll", "Saved Last Seen - Juz: $juzNumber, Surah: $surahNumber, Ayah: $lastAyah")
                        }
                    }
                }
            }
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
                        Icon(painter = painterResource(id = R.drawable.arrowback), contentDescription = "Kembali")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(painter = painterResource(id = R.drawable.qur6), contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color(0xFF1E1E1E))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Pilih Qari", color = Color.White) },
                                onClick = {
                                    showQariDialog = true
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Cari Surah dan Ayat", color = Color.White) },
                                onClick = {
                                    showSearchDialog = true
                                    showMenu = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF100F0F),
                    titleContentColor = Color(0xFFFDFAFA),
                    navigationIconContentColor = Color(0xFF472694),
                    actionIconContentColor = Color(0xFF03A9F4)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0E0E0E), Color(0xFF090808), Color(0xFF0E0E0E))
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
                            val numberOfAyahs = surah?.numberOfAyahs ?: 0

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
                                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
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

        if (showSearchDialog) {
            SearchSurahAndAyahDialog(
                juzNumber = juzNumber,
                viewModel = viewModel,
                onDismiss = { showSearchDialog = false },
                onSearch = { surahNumber, ayahNumber ->
                    coroutineScope.launch {
                        viewModel.fetchJuzDetail(juzNumber, reset = true, targetSurahNumber = surahNumber, targetAyahNumber = ayahNumber)
                        while (viewModel.isLoading.value) {
                            delay(100)
                        }
                        val groupedBySurah = juzDetail.groupBy { it.surah.number }
                        val surahIndex = groupedBySurah.entries.indexOfFirst { it.key == surahNumber }
                        if (surahIndex != -1) {
                            val ayahsInSurah = groupedBySurah[surahNumber] ?: emptyList()
                            val ayahExists = ayahsInSurah.any { it.numberInSurah == ayahNumber }
                            if (ayahExists) {
                                listState.scrollToItem(surahIndex)
                                Log.d("JuzSearch", "Scrolled to Surah: $surahNumber, Ayah: $ayahNumber, Surah Index: $surahIndex")
                            } else {
                                snackbarHostState.showSnackbar("Ayat $ayahNumber tidak ditemukan di Surah $surahNumber")
                                Log.e("JuzSearch", "Ayah $ayahNumber not found in Surah $surahNumber in Juz $juzNumber")
                            }
                        } else {
                            snackbarHostState.showSnackbar("Surah $surahNumber tidak ditemukan di Juz $juzNumber")
                            Log.e("JuzSearch", "Surah $surahNumber not found in Juz $juzNumber")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSurahAndAyahDialog(
    juzNumber: Int,
    viewModel: JuzViewModel,
    onDismiss: () -> Unit,
    onSearch: (Int, Int) -> Unit
) {
    var selectedSurah by remember { mutableStateOf<String?>(null) }
    var ayahNumber by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val surahList = remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(juzNumber) {
        val repository = QuranRepository()
        try {
            val response = repository.getJuz(juzNumber)
            if (response.code == 200) {
                surahList.value = response.data.ayahs
                    .groupBy { it.surah.number }
                    .map { entry ->
                        val surah = entry.value.first().surah
                        "${surah.number}. ${surah.englishName}"
                    }
                Log.d("SearchDialog", "Loaded surah list for Juz $juzNumber: ${surahList.value}")
            } else {
                Log.e("SearchDialog", "Failed to load surah list for Juz $juzNumber: ${response.status}")
            }
        } catch (e: Exception) {
            Log.e("SearchDialog", "Error loading surah list: ${e.message}")
        }
    }

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
                Text("Cari Surah dan Ayat", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown untuk Surah
                Box {
                    OutlinedTextField(
                        value = selectedSurah ?: "Pilih Surah",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Surah", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color.White
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF03A9F4),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color.White
                        )
                    )
                    DropdownMenu(
                        expanded = expanded && surahList.value.isNotEmpty(),
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(Color(0xFF1E1E1E))
                            .fillMaxWidth()
                    ) {
                        surahList.value.forEach { surahName ->
                            DropdownMenuItem(
                                text = { Text(surahName, color = Color.White) },
                                onClick = {
                                    selectedSurah = surahName
                                    expanded = false
                                }
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(enabled = surahList.value.isNotEmpty()) { expanded = true }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input untuk Nomor Ayat
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
                        onClick = {
                            val surahNumber = selectedSurah?.substringBefore(".")?.toIntOrNull() ?: return@TextButton
                            val ayahNum = ayahNumber.toIntOrNull() ?: return@TextButton
                            if (ayahNum > 0) onSearch(surahNumber, ayahNum)
                        },
                        enabled = selectedSurah != null && ayahNumber.isNotEmpty()
                    ) {
                        Text("Cari", color = if (selectedSurah != null && ayahNumber.isNotEmpty()) Color.White else Color.Gray)
                    }
                }
            }
        }
    }
}
package com.example.utsquranappq.activity

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quranapp.viewmodel.SurahViewModel
import com.example.utsquranappq.R
import com.example.utsquranappq.activity.homescreen.GreetingSection
import com.example.utsquranappq.activity.homescreen.TabSection
import com.example.utsquranappq.model.Surah
import com.example.utsquranappq.model.juzListStatic
import com.example.utsquranappq.ui.juzuI.juzscreen.JuzTab
import com.example.utsquranappq.ui.surahui.surahscreen.SurahTab
import com.example.utsquranappq.utils.cancelAdhan
import com.example.utsquranappq.utils.createNotificationChannel
import com.example.utsquranappq.utils.getTranslation
import com.example.utsquranappq.utils.scheduleAdhan
import com.example.utsquranappq.utils.scheduleReminder
import com.example.utsquranappq.work.QuranReminderReceiver
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun HomeScreen(navController: NavController, surahViewModel: SurahViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    var showSettings by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("QuranPrefs", Context.MODE_PRIVATE)
    var reminderTime by remember { mutableStateOf(prefs.getString("reminderTime", "08:00") ?: "08:00") }
    var isAdhanEnabled by remember { mutableStateOf(prefs.getBoolean("isAdhanPrayerEnabled", false)) }
    val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    // Last Seen Data
    val lastSurah by remember { mutableStateOf(prefs.getInt("lastSurah", -1)) }
    val lastAyah by remember { mutableStateOf(prefs.getInt("lastAyah", -1)) }
    val lastJuz by remember { mutableStateOf(prefs.getInt("lastJuz", -1)) }
    val lastJuzSurah by remember { mutableStateOf(prefs.getInt("lastJuzSurah", -1)) }
    val lastJuzAyah by remember { mutableStateOf(prefs.getInt("lastJuzAyah", -1)) }

    val surahList by surahViewModel.surahList.collectAsState()
    val matchedResults = remember(searchQuery, surahList) {
        (surahList.filter { it.name.contains(searchQuery, true) || it.englishName.contains(searchQuery, true) }
            .map { "${it.number}. ${getTranslation(it.englishName, "", "").first}" to "surahDetail/${it.number}" }) +
                juzListStatic.filter { "Juz ${it.number}".contains(searchQuery, true) }
                    .map { "Juz ${it.number}" to "juz_detail/${it.number}" }
    }

    LaunchedEffect(Unit) { createNotificationChannel(context) }
    LaunchedEffect(reminderTime, hasPermission) {
        if (hasPermission) {
            prefs.edit().putString("reminderTime", reminderTime).apply()
            scheduleReminder(context, "quran", reminderTime, 0)
        }
    }
    LaunchedEffect(isAdhanEnabled, hasPermission) {
        if (hasPermission) {
            prefs.edit().putBoolean("isAdhanPrayerEnabled", isAdhanEnabled).apply()
            if (isAdhanEnabled) scheduleAdhan(context) else cancelAdhan(context)
        }
    }

    Scaffold(
        topBar = {
            TopBar(searchQuery, { searchQuery = it }, { navController.navigate(it); searchQuery = "" },
                searchQuery.isNotEmpty() && matchedResults.isNotEmpty(), matchedResults, { showSettings = true })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(Color(0xFF0C0C1F), Color(0xFF050B2C), Color(0xFF06062D)), Offset(0f, 0f), Offset(1900f, 880f)))
                .padding(padding)
        ) {
            GreetingSection(lastSurah, lastAyah, lastJuz, lastJuzSurah, lastJuzAyah, surahList, navController)
            TabSection(navController)
        }
        if (showSettings) {
            SettingsDialog(reminderTime, { reminderTime = it }, isAdhanEnabled, { isAdhanEnabled = it }, { showSettings = false }, hasPermission)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    searchQuery: String, onSearchChange: (String) -> Unit, onSearchClick: (String) -> Unit,
    showResults: Boolean, results: List<Pair<String, String>>, onMenuClick: () -> Unit
) {
    var isSearching by remember { mutableStateOf(false) }
    Column {
        TopAppBar(
            title = {
                if (isSearching) {
                    TextField(
                        value = searchQuery, onValueChange = onSearchChange, placeholder = { Text("Cari Surah/Juz", color = Color.Gray) },
                        singleLine = true, colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent, cursorColor = Color.White,
                            focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                        ), modifier = Modifier.fillMaxWidth()
                    )
                } else Text("Al-Qur'an", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            },
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Image(
                        painter = painterResource(id = R.drawable.setting), // ganti dengan nama resource lokal kamu
                        contentDescription = "Menu",
                        modifier = Modifier.size(23.dp) // ukuran bisa disesuaikan
                    )
                }
            },
            actions = { IconButton(onClick = { isSearching = !isSearching })

            {
                Image(
                    painter = painterResource(id = R.drawable.search), // ganti dengan ikon lokal kamu
                    contentDescription = "Search",
                    modifier = Modifier.size(24.dp) // atur ukuran sesuai kebutuhan
                ) }

                      },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F0E2A))
        )
        if (isSearching && showResults) {
            LazyColumn(modifier = Modifier.fillMaxWidth().background(Color(0xFF1E1E1E))) {
                items(results) { (name, route) ->
                    Text(name, modifier = Modifier.fillMaxWidth().clickable { onSearchClick(route) }.padding(16.dp), color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    reminderTime: String, onTimeChange: (String) -> Unit, isAdhanEnabled: Boolean,
    onAdhanToggle: (Boolean) -> Unit, onDismiss: () -> Unit, hasPermission: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss, title = { Text("Pengaturan Pengingat", color = Color.White) },
        text = {
            Column {
                if (!hasPermission) Text("Izin notifikasi diperlukan.", color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
                Text("Waktu Pengingat Harian", color = Color.White)
                SimpleTimePicker(reminderTime, onTimeChange)
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Aktifkan Adzan Sholat", color = Color.White)
                    Switch(checked = isAdhanEnabled, onCheckedChange = onAdhanToggle, enabled = hasPermission)
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("OK", color = Color.White) } },
        containerColor = Color(0xFF1E1E1E), textContentColor = Color.White
    )
}

@Composable
fun SimpleTimePicker(initialTime: String, onTimeChange: (String) -> Unit) {
    val context = LocalContext.current
    var time by remember { mutableStateOf(initialTime) }
    Button(onClick = {
        val (hour, minute) = time.split(":").map { it.toIntOrNull() ?: 0 }
        TimePickerDialog(context, { _, h, m ->
            time = String.format("%02d:%02d", h, m); onTimeChange(time)
        }, hour, minute, true).show()
    }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
        Text("Pilih Waktu: $time", color = Color.White, fontSize = 16.sp)
    }
}


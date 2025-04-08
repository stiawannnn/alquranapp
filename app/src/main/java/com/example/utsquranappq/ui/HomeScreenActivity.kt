package com.example.utsquranappq.ui

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.utsquranappq.model.Surah
import com.example.utsquranappq.model.juzListStatic
import com.example.utsquranappq.ui.juzuI.juzscreen.JuzTab
import com.example.utsquranappq.ui.surahui.surahscreen.SurahTab
import com.example.utsquranappq.utiils.getTranslation
import com.example.utsquranappq.work.QuranReminderReceiver
import kotlinx.coroutines.delay
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
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
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
            navigationIcon = { IconButton(onClick = onMenuClick) { Icon(Icons.Filled.Menu, "Menu", tint = Color.White) } },
            actions = { IconButton(onClick = { isSearching = !isSearching }) { Icon(Icons.Filled.Search, "Search", tint = Color.White) } },
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
        android.app.TimePickerDialog(context, { _, h, m ->
            time = String.format("%02d:%02d", h, m); onTimeChange(time)
        }, hour, minute, true).show()
    }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
        Text("Pilih Waktu: $time", color = Color.White, fontSize = 16.sp)
    }
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = android.app.NotificationChannel("quran_channel", "Quran Reminder", android.app.NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Pengingat Al-Qur'an dan Adzan"
            setShowBadge(true)
            enableLights(true)
            enableVibration(true)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            // Tidak ada setSound di sini untuk menghindari suara default
        }
        context.getSystemService(android.app.NotificationManager::class.java).createNotificationChannel(channel)
    }
}

fun scheduleReminder(context: Context, type: String, time: String, requestCode: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, QuranReminderReceiver::class.java).apply {
        putExtra("type", type)
        putExtra("prayerTime", time)
        if (type == "adhan") putExtra("prayerName", getPrayerName(time))
        if (type == "adhan") putExtra("isAdhanEnabled", true)
    }
    val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    val calendar = Calendar.getInstance().apply {
        val (hours, minutes) = time.split(":").map { it.toInt() }
        set(Calendar.HOUR_OF_DAY, hours)
        set(Calendar.MINUTE, minutes)
        set(Calendar.SECOND, 0)
        if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
    }

    val triggerTime = calendar.timeInMillis
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && alarmManager.canScheduleExactAlarms()) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    } else {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }
}

fun scheduleAdhan(context: Context) {
    val prayerTimes = listOf("04:59" to "Subuh", "12:20" to "Dzuhur", "15:30" to "Ashar", "18:23" to "Maghrib", "19:32" to "Isya")
    prayerTimes.forEachIndexed { index, (time, _) -> scheduleReminder(context, "adhan", time, index + 1) }
}

fun cancelAdhan(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val prayerTimes = listOf("04:59" to "Subuh", "12:20" to "Dzuhur", "15:30" to "Ashar", "18:23" to "Maghrib", "19:32" to "Isya")
    prayerTimes.forEachIndexed { index, (time, name) ->
        val intent = Intent(context, QuranReminderReceiver::class.java).apply {
            putExtra("type", "adhan")
            putExtra("prayerTime", time)
            putExtra("prayerName", name)
            putExtra("isAdhanEnabled", true)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, index + 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }
}

private fun getPrayerName(time: String): String = when (time) {
    "04:59" -> "Subuh"
    "12:20" -> "Dzuhur"
    "15:30" -> "Ashar"
    "18:23" -> "Maghrib"
    "19:32" -> "Isya"
    else -> "Unknown"
}
fun saveLastSeen(context: Context, surah: Int? = null, ayah: Int? = null, juz: Int? = null, juzSurah: Int? = null, juzAyah: Int? = null) {
    val prefs = context.getSharedPreferences("QuranPrefs", Context.MODE_PRIVATE)
    with(prefs.edit()) {
        if (surah != null && ayah != null) {
            putInt("lastSurah", surah)
            putInt("lastAyah", ayah)
            putInt("lastJuz", -1)
            putInt("lastJuzSurah", -1)
            putInt("lastJuzAyah", -1)
        } else if (juz != null && juzSurah != null && juzAyah != null) {
            putInt("lastJuz", juz)
            putInt("lastJuzSurah", juzSurah)
            putInt("lastJuzAyah", juzAyah)
            putInt("lastSurah", -1)
            putInt("lastAyah", -1)
        }
        apply()
    }
}
// Fungsi lain (GreetingSection, LastReadSection, TabSection, BottomNavigationBar) tetap sama
@Composable
fun GreetingSection(
    lastSurah: Int, lastAyah: Int, lastJuz: Int, lastJuzSurah: Int, lastJuzAyah: Int,
    surahList: List<Surah>, navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 11.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "السَّلاَمُ عَلَيْكُمْ",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.hafs)),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Image(
            painter = painterResource(id = R.drawable.quran1),
            contentDescription = "Greeting Icon",
            modifier = Modifier
                .size(107.dp)
                .padding(top = 0.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LastSeenSection(lastSurah, lastAyah, lastJuz, lastJuzSurah, lastJuzAyah, surahList, navController)
    }
}

@Composable
fun LastSeenSection(
    lastSurah: Int, lastAyah: Int, lastJuz: Int, lastJuzSurah: Int, lastJuzAyah: Int,
    surahList: List<Surah>, navController: NavController
) {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = getCurrentTime()
        }
    }

    val lastSeenText = when {
        lastSurah != -1 && lastAyah != -1 -> {
            val surahName = surahList.find { it.number == lastSurah }?.let { getTranslation(it.englishName, "", "").first } ?: "Unknown"
            "$surahName - Ayat $lastAyah"
        }
        lastJuz != -1 && lastJuzSurah != -1 && lastJuzAyah != -1 -> {
            val surahName = surahList.find { it.number == lastJuzSurah }?.let { getTranslation(it.englishName, "", "").first } ?: "Unknown"
            "Juz $lastJuz - $surahName, Ayat $lastJuzAyah"
        }
        else -> "Belum ada riwayat"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(144.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF673AB7), Color(0xFFC49CC9), Color(0xFFC804EA))
                ),
                shape = RoundedCornerShape(23.dp)
            )
            .clickable {
                when {
                    lastSurah != -1 -> navController.navigate("surahDetail/$lastSurah")
                    lastJuz != -1 -> navController.navigate("juz_detail/$lastJuz")
                }
            }
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(id = R.drawable.prayicon), contentDescription = "Book")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Last Read", color = Color.White, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(lastSeenText, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(currentTime, color = Color.White, fontSize = 14.sp)
        }
    }
}

fun getCurrentTime(): String {
    val locale = Locale.getDefault()
    val timeZone = TimeZone.getDefault()
    val dateFormat = java.text.SimpleDateFormat("EEEE, dd MMMM yyyy hh:mm:ss a", locale)
    dateFormat.timeZone = timeZone
    return dateFormat.format(Date())
}

@Composable
fun TabSection(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Surah", "Juz")

    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color(0xFF0D1B2A),
            contentColor = Color.White
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedTabIndex == index) Color.White else Color.Gray
                        )
                    }
                )
            }
        }
        when (selectedTabIndex) {
            0 -> SurahTab(navController)
            1 -> JuzTab(navController)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(containerColor = Color(0xFF150D23)) {
        val items = listOf("Jadwal", "Qiblat", "Quran", "Bookmark", "Tajweed")
        val icons = listOf(
            R.drawable.prayicon, R.drawable.calendar,
            R.drawable.qur5, R.drawable.prayicon, R.drawable.prayicon
        )
        var selectedItem by remember { mutableStateOf(2) }

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Image(
                        painter = painterResource(id = icons[index]),
                        contentDescription = item,
                        modifier = Modifier.size(23.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    when (index) {
                        0 -> navController.navigate("sholat")
                        1 -> navController.navigate("Qiblat")
                        2 -> navController.navigate("home")
                        3 -> navController.navigate("bookmark")
                        4 -> navController.navigate("info")
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF03A9F4),
                    unselectedIconColor = Color(0xFFB0BEC5),
                    indicatorColor = Color(0xFF0B2A80),
                    selectedTextColor = Color(0xFF2A7FE0),
                    unselectedTextColor = Color(0xFFB0BEC5)
                )
            )
        }
    }
}
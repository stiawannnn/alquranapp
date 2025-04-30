
package app.dev.quranappbystiawan.activity
import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import app.dev.quranapp.viewmodel.SurahViewModel
import app.dev.quranappbystiawan.activity.homescreen.GreetingSection
import app.dev.quranappbystiawan.activity.homescreen.TabSection
import app.dev.quranappbystiawan.model.juzListStatic
import app.dev.quranappbystiawan.utils.FirebaseUtils
import app.dev.quranappbystiawan.utils.cancelAdhan
import app.dev.quranappbystiawan.utils.createNotificationChannel
import app.dev.quranappbystiawan.utils.getTranslation
import app.dev.quranappbystiawan.utils.scheduleAdhan
import app.dev.quranappbystiawan.utils.scheduleReminder
import app.dev.quranappbystiawan.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import coil.compose.AsyncImage

@Composable
fun HomeScreen(navController: NavController, surahViewModel: SurahViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    var showSettings by remember { mutableStateOf(false) }
    var showUserInfo by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf(FirebaseUtils.getCurrentUser()) } // State untuk melacak pengguna
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("QuranPrefs", Context.MODE_PRIVATE)
    var reminderTime by remember { mutableStateOf(prefs.getString("reminderTime", "08:00") ?: "08:00") }
    var isAdhanEnabled by remember { mutableStateOf(prefs.getBoolean("isAdhanPrayerEnabled", false)) }
    val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
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

    // Launcher untuk ganti akun
    val googleSignInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(Exception::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseUtils.auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    // Perbarui state pengguna setelah ganti akun berhasil
                    currentUser = FirebaseUtils.getCurrentUser()
                    showUserInfo = false // Tutup dialog setelah ganti akun
                }
            }
        } catch (e: Exception) {
            // Tangani error (misal, tampilkan toast)
        }
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
            TopBar(
                searchQuery,
                { searchQuery = it },
                { navController.navigate(it); searchQuery = "" },
                searchQuery.isNotEmpty() && matchedResults.isNotEmpty(),
                matchedResults,
                { showSettings = true },
                { showUserInfo = true },
                currentUser
            )
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
        if (showUserInfo) {
            UserInfoDialog(
                onLogout = {
                    FirebaseUtils.signOut(context) {
                            val intent = Intent(context, SplashScreenActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                    }
                },
                onChangeAccount = {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .requestProfile()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    googleSignInClient.signOut().addOnCompleteListener {
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    }
                },
                onDismiss = { showUserInfo = false },
                onBack = { showUserInfo = false },
                user = currentUser
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSearchClick: (String) -> Unit,
    showResults: Boolean,
    results: List<Pair<String, String>>,
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit,
    user: FirebaseUser?
) {
    var isSearching by remember { mutableStateOf(false) }
    Column {
        TopAppBar(
            title = {
                if (isSearching) {
                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text("Cari Surah/Juz", color = Color.Gray) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else Text(
                    "Al-Qur'an",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Image(
                        painter = painterResource(id = R.drawable.setting),
                        contentDescription = "Menu",
                        modifier = Modifier.size(23.dp)
                    )
                }
            },
            actions = {
                IconButton(onClick = { isSearching = !isSearching }) {
                    Image(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "Cari",
                        modifier = Modifier.size(24.dp)
                    )
                }
                if (user != null) {
                    IconButton(onClick = onProfileClick) {
                        AsyncImage(
                            model = user.photoUrl,
                            contentDescription = "Profil",
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.White, CircleShape)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F0E2A))
        )
        if (isSearching && showResults) {
            LazyColumn(modifier = Modifier.fillMaxWidth().background(Color(0xFF1E1E1E))) {
                items(results) { (name, route) ->
                    Text(
                        name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSearchClick(route) }
                            .padding(16.dp),
                        color = Color.White
                    )
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

@Composable
fun UserInfoDialog(
    onLogout: () -> Unit,
    onChangeAccount: () -> Unit,
    onDismiss: () -> Unit,
    onBack: () -> Unit,
    user: FirebaseUser?
) {
    var showLogoutConfirmation by remember { mutableStateOf(false) }
    val displayName = user?.displayName ?: "Tidak ada nama"
    val email = user?.email ?: "Tidak ada email"

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.Black
                        )
                    }

                    Text(
                        text = "Info Profil",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (!user?.photoUrl?.toString().isNullOrBlank()) {
                        AsyncImage(
                            model = user?.photoUrl,
                            contentDescription = "Foto Profil",
                            modifier = Modifier
                                .size(111.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Icon",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = displayName,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                            Text(
                                text = email,
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = { showLogoutConfirmation = true }) {
                            Text(
                                text = "Keluar",
                                color = Color(0xFF512DA8),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        TextButton(onClick = onChangeAccount) {
                            Text(
                                text = "Ganti Akun",
                                color = Color(0xFF512DA8),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = {
                Text("Konfirmasi Logout")
            },
            text = {
                Text("Apakah Anda yakin ingin keluar?")
            },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutConfirmation = false
                    onLogout()
                }) {
                    Text("Ya")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

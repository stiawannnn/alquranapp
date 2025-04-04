package com.example.utsquranappq.ui
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import java.util.Locale
import java.util.Date
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.utsquranappq.R
import kotlinx.coroutines.delay



@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = { TopBar() }, // Pass navController
    ) { padding ->
        // Membuat brush dengan gradien 3 warna
        val gradientBrush = Brush.linearGradient(
            colors = listOf(Color(0xFF0C0C1F), Color(0xFF050B2C), Color(0xFF06062D)),
            start = Offset(0f, 0f),
            end = Offset(1900f, 880f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)  // Menggunakan gradien sebagai background
                .padding(padding)
        ) {
            GreetingSection()
            TabSection(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = { Text("Al- Qur'an", fontSize = 20.sp, fontWeight = FontWeight.Bold,color = Color.White) },
        navigationIcon = {
            IconButton(onClick = { /* Handle Menu Click */ }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
            }
        },
        actions = {
            IconButton(onClick = { /* Handle Search Click */ }) {
                Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F0E2A))
    )
}

@Composable
fun GreetingSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 11.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Ini yang membuat semua child di tengah
    ) {
        Text(
            text = "السَّلاَمُ عَلَيْكُمْ",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.hafs)),
            color = Color.White,
            textAlign = TextAlign.Center, // Untuk memastikan teks arab rata tengah
            modifier = Modifier.fillMaxWidth() // Agar textAlign bekerja optimal
        )
        Image(
            painter = painterResource(id = R.drawable.quran1),
            contentDescription = "Greeting Icon",
            modifier = Modifier
                .size(107.dp)
                .padding(top = 0.dp)
        )

        Spacer(modifier = Modifier.height(16.dp)) // Jarak antara gambar dan LastReadSection

        LastReadSection()
    }
}
@Composable
fun LastReadSection() {
    // Menyimpan waktu dalam state agar bisa diupdate
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    // Memperbarui waktu setiap detik
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)  // Delay 1 detik
            currentTime = getCurrentTime()  // Update waktu
        }
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
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(id = R.drawable.prayicon), contentDescription = "Book")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Last Read", color = Color.White, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Al-Fatihah", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Ayat No: 1", color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(currentTime, color = Color.White, fontSize = 14.sp) // Tampilkan waktu yang diperbarui
        }
    }
}

// Fungsi untuk mendapatkan waktu saat ini
fun getCurrentTime(): String {
    val locale = Locale.getDefault()
    val timeZone = TimeZone.getDefault()
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy hh:mm:ss a", locale)  // Format: Senin, 01 April 2025 12:45:30
    dateFormat.timeZone = timeZone
    return dateFormat.format(Date())  // Format tanggal dan waktu
}



@Composable
fun TabSection(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Surah", "Page", "Juz", "Hizb", "Ruku")

    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color(0xFF0D1B2A), // Warna latar TabRow
            contentColor = Color.White // Warna indikator tab terpilih
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
            1 -> HalamanTab()
            2 -> JuzTab()
            3 -> HizbTab()
            4 -> RukuTab()
        }
    }
}



@Composable
fun HalamanTab() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("page", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}
@Composable
fun JuzTab() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("juzz", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}
@Composable
fun HizbTab() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("juzz", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}
@Composable
fun RukuTab() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("juzz", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(containerColor = Color(0xFF150D23)) {
        val items = listOf("Quran", "Tips", "Quran", "Doa", "Bookmark")
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
                    // Navigate to the corresponding screen
                    when (index) {
                        0 -> navController.navigate("quran") // Navigate to Quran screen
                        1 -> navController.navigate("tips") // Navigate to Tips screen (optional)
                        2 -> navController.navigate("home") // Back to home screen
                        // Add more navigation cases as needed
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
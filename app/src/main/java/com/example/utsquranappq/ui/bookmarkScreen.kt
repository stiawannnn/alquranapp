package com.example.utsquranappq.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.utsquranappq.R
import com.example.utsquranappq.model.Bookmark
import com.example.utsquranappq.viewmodel.BookmarkViewModel

@Composable
fun BookmarkScreen(navController: NavController? = null) {
    val context = LocalContext.current
    val viewModel: BookmarkViewModel = viewModel(factory = BookmarkViewModelFactory(context))
    val bookmarks by viewModel.bookmarks.collectAsState()

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2A41BE),
            Color(0xFF2AB8D0),
            Color(0xFF9B3CBD)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Daftar Bookmark",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (bookmarks.isEmpty()) {
            Text(
                text = "Belum ada bookmark",
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                items(bookmarks) { bookmark ->
                    BookmarkItem(bookmark, navController, viewModel::removeBookmark)
                }
            }
        }
    }
}

@Composable
fun BookmarkItem(bookmark: Bookmark, navController: NavController?, onRemove: (Int, Int) -> Unit) {
    var showDialog by remember { mutableStateOf(false) } // State untuk menampilkan dialog

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                if (bookmark.juzNumber != null) {
                    navController?.navigate(
                        "juz_detail/${bookmark.juzNumber}?surah=${bookmark.surahNumber}&ayah=${bookmark.ayahNumber}"
                    )
                } else {
                    navController?.navigate(
                        "surahDetail/${bookmark.surahNumber}?ayah=${bookmark.ayahNumber}"
                    )
                }
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${bookmark.surahName}, Ayat ${bookmark.ayahNumber}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                bookmark.juzNumber?.let {
                    Text(
                        text = "Juz $it",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
            IconButton(onClick = { showDialog = true }) { // Tampilkan dialog saat klik hapus
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_delete),
                    contentDescription = "Hapus Bookmark",
                    tint = Color.Red
                )
            }
        }
    }

    // Dialog konfirmasi hapus
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus bookmark ${bookmark.surahName}, Ayat ${bookmark.ayahNumber}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemove(bookmark.surahNumber, bookmark.ayahNumber) // Hapus bookmark
                        showDialog = false
                    }
                ) {
                    Text("Ya", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Tidak", color = Color.White)
                }
            },
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}

class BookmarkViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookmarkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookmarkViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
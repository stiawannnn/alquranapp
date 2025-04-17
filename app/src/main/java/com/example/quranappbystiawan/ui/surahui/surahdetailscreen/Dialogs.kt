package com.example.quranappbystiawan.ui.surahui.surahdetailscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.quranappbystiawan.model.AyahEdition

@Composable
fun VoiceSelectionDialog(
    onDismiss: () -> Unit,
    surahDetail: List<AyahEdition>,
    onQariSelected: (String?) -> Unit
) {
    val qariList = surahDetail.filter { it.edition.language == "ar" && it.audio != null }
        .map { it.edition }.distinctBy { it.identifier }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Suara Qari") },
        text = {
            LazyColumn {
                item {
                    Text(
                        text = "Semua Qari",
                        modifier = Modifier.fillMaxWidth().clickable { onQariSelected(null); onDismiss() }.padding(8.dp)
                    )
                }
                items(qariList) { qari ->
                    Text(
                        text = qari.englishName,
                        modifier = Modifier.fillMaxWidth().clickable { onQariSelected(qari.identifier); onDismiss() }.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Tutup") } }
    )
}

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
        confirmButton = { TextButton(onClick = { onSearch(ayahNumber); onDismiss() }) { Text("Cari") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}
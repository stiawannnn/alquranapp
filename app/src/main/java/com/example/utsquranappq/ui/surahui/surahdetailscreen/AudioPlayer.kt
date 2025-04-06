package com.example.utsquranappq.ui.surahui.surahdetailscreen

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.utsquranappq.model.AyahEdition

@Composable
fun AudioPlayer(
    audioUrl: String,
    ayahNumber: Int,
    qariName: String,
    allAyahs: List<AyahEdition>,
    selectedQari: String?,
    onAyahPlaying: (Int) -> Unit,
    isPlayingAll: Boolean
) {
    val mediaPlayer = remember { MediaPlayer() }
    var isPlaying by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }

    LaunchedEffect(audioUrl) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(audioUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener { isPrepared = true }
            mediaPlayer.setOnCompletionListener {
                isPlaying = false
                mediaPlayer.seekTo(0)
                onAyahPlaying(0)
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error preparing audio: ${e.message}")
        }
    }

    DisposableEffect(Unit) {
        onDispose { mediaPlayer.release() }
    }

    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Audio Ayat $ayahNumber ($qariName)",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                if (isPrepared && !isPlayingAll) {
                    if (isPlaying) {
                        mediaPlayer.pause()
                        isPlaying = false
                        onAyahPlaying(0)
                    } else {
                        mediaPlayer.start()
                        isPlaying = true
                        onAyahPlaying(ayahNumber)
                    }
                }
            },
            enabled = isPrepared && !isPlayingAll,
            colors = ButtonDefaults.buttonColors(containerColor = if (isPlaying) Color.Red else Color.Green)
        ) {
            Text(text = if (isPlaying) "Pause" else "Play", color = Color.White)
        }
    }
}
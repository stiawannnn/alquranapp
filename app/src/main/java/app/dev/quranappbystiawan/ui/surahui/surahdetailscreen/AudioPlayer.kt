package app.dev.quranappbystiawan.ui.surahui.surahdetailscreen

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.dev.quranappbystiawan.model.AyahEdition
import app.dev.quranappbystiawan.R

@Composable
fun AudioPlayer(
    audioUrl: String,
    ayahNumber: Int,
    qariName: String,
    allAyahs: List<AyahEdition>,
    selectedQari: String?,
    onAyahPlaying: (Int) -> Unit,
    isPlayingAll: Boolean,
    modifier: Modifier = Modifier // Tambah ini
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

    Row(
        modifier = modifier.padding(top = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Qari : $qariName",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
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
            enabled = isPrepared && !isPlayingAll
        ) {
            Icon(
                painter = painterResource(id = if (isPlaying) R.drawable.pause else R.drawable.play),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.Unspecified, // biar warna asli gambar tidak berubah
                modifier = Modifier.size(33.dp)
            )
        }
    }
}


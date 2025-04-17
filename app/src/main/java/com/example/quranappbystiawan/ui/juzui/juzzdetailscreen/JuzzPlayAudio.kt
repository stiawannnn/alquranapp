package com.example.quranappbystiawan.ui.juzui.juzzdetailscreen

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.quranappbystiawan.model.AyahEdition
import kotlinx.coroutines.delay

class JuzAudioManager {
    private val mediaPlayer = MediaPlayer()
    val isPlayingAll = mutableStateOf(false)
    val isPaused = mutableStateOf(false)

    suspend fun playAll(
        ayahs: List<AyahEdition>,
        selectedQari: String?,
        allAyahs: List<AyahEdition>,
        startIndex: Int,
        onAyahPlaying: (Int) -> Unit,
        onFinished: () -> Unit
    ) {
        try {
            isPlayingAll.value = true
            val audioAyahs = allAyahs
                .drop(startIndex) // Mulai dari startIndex
                .filter { it.audio != null && (selectedQari == null || it.edition.identifier == selectedQari) }
                .distinctBy { it.number } // Hindari duplikat berdasarkan nomor ayat penuh

            for (ayah in audioAyahs) {
                if (!isPlayingAll.value) break
                mediaPlayer.reset()
                mediaPlayer.setDataSource(ayah.audio!!)
                mediaPlayer.prepare()
                onAyahPlaying(ayah.numberInSurah)
                mediaPlayer.start()
                Log.d("JuzAudioManager", "Playing audio for ayah ${ayah.numberInSurah} (surah ${ayah.surah.number}, ${ayah.edition.englishName})")

                var pausedPosition = 0
                while (mediaPlayer.isPlaying || isPaused.value) {
                    if (isPaused.value) {
                        pausedPosition = mediaPlayer.currentPosition
                        mediaPlayer.pause()
                        while (isPaused.value && isPlayingAll.value) delay(100)
                        if (!isPlayingAll.value) break
                        mediaPlayer.seekTo(pausedPosition)
                        mediaPlayer.start()
                    }
                    delay(100)
                }
            }
        } catch (e: Exception) {
            Log.e("JuzAudioManager", "Error playing all audio: ${e.message}")
        } finally {
            if (!isPaused.value) mediaPlayer.reset()
            isPlayingAll.value = false
            isPaused.value = false
            onFinished()
        }
    }

    fun pause() {
        if (isPlayingAll.value && !isPaused.value) {
            mediaPlayer.pause()
            isPaused.value = true
            Log.d("JuzAudioManager", "Paused audio")
        }
    }

    fun resume() {
        if (isPlayingAll.value && isPaused.value) {
            mediaPlayer.start()
            isPaused.value = false
            Log.d("JuzAudioManager", "Resumed audio")
        }
    }

    fun stop() {
        if (isPlayingAll.value) {
            mediaPlayer.stop()
            mediaPlayer.reset()
            isPlayingAll.value = false
            isPaused.value = false
            Log.d("JuzAudioManager", "Stopped audio")
        }
    }

    fun release() {
        mediaPlayer.release()
    }
}
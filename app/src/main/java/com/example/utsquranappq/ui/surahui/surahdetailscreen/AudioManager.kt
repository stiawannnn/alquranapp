package com.example.utsquranappq.ui.surahui.surahdetailscreen

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.utsquranappq.model.AyahEdition
import kotlinx.coroutines.delay

class AudioManager {
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
            val ayahNumbers = allAyahs.groupBy { it.numberInSurah }.keys.filter { it >= startIndex }.sorted()
            for (ayahNumber in ayahNumbers) {
                if (!isPlayingAll.value) break
                val currentAyahs = allAyahs.filter { it.numberInSurah == ayahNumber }
                val audioAyahs = currentAyahs.filter { ayah ->
                    ayah.audio != null && (selectedQari == null || ayah.edition.identifier == selectedQari)
                }
                for (ayah in audioAyahs) {
                    if (!isPlayingAll.value) break
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(ayah.audio!!)
                    mediaPlayer.prepare()
                    onAyahPlaying(ayah.numberInSurah)
                    mediaPlayer.start()
                    Log.d("PlayAllAudio", "Playing audio for ayah $ayahNumber (${ayah.edition.englishName})")

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
            }
        } catch (e: Exception) {
            Log.e("PlayAllAudio", "Error playing all audio: ${e.message}")
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
        }
    }

    fun resume() {
        if (isPlayingAll.value && isPaused.value) {
            mediaPlayer.start()
            isPaused.value = false
        }
    }

    fun stop() {
        if (isPlayingAll.value) {
            mediaPlayer.stop()
            mediaPlayer.reset()
            isPlayingAll.value = false
            isPaused.value = false
        }
    }

    fun release() {
        mediaPlayer.release()
    }
}
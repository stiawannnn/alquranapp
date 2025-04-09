package com.example.utsquranappq.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.utsquranappq.model.AyahEdition
import com.example.utsquranappq.repository.QuranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JuzViewModel : ViewModel() {
    private val repository = QuranRepository()

    private val _juzDetail = MutableStateFlow<List<AyahEdition>>(emptyList())
    val juzDetail: StateFlow<List<AyahEdition>> = _juzDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var currentPage = 0
    private val pageSize = 10 // Memuat 10 ayat per halaman
    private var totalAyahs = 0
    private var juzNumberLoaded = -1

    fun fetchJuzDetail(
        juzNumber: Int,
        reset: Boolean = false,
        targetSurahNumber: Int? = null,
        targetAyahNumber: Int? = null
    ) {
        if (reset || juzNumber != juzNumberLoaded) {
            currentPage = 0
            _juzDetail.value = emptyList()
            juzNumberLoaded = juzNumber
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val editions = "quran-tajweed,en.transliteration,id.indonesian,ar.alafasy,ar.abdurrahmaansudais,ar.husary,ar.minshawi,ar.ahmedajamy"
                val response = repository.getJuz(juzNumber)
                if (response.code == 200) {
                    val ayahs = response.data.ayahs
                    totalAyahs = ayahs.size
                    Log.d("JuzDetail", "Juz $juzNumber fetched: $totalAyahs ayahs")

                    val startIndex = if (targetSurahNumber != null && targetAyahNumber != null) {
                        val targetIndex = ayahs.indexOfFirst {
                            it.surah.number == targetSurahNumber && it.numberInSurah == targetAyahNumber
                        }
                        if (targetIndex != -1) {
                            // Pastikan ayat target berada di tengah halaman (jika memungkinkan)
                            val adjustedStart = maxOf(0, targetIndex - (pageSize / 2))
                            val targetPage = adjustedStart / pageSize
                            currentPage = targetPage
                            val calculatedStart = targetPage * pageSize
                            Log.d("JuzDetail", "Jumping to page $targetPage for Surah $targetSurahNumber, Ayah $targetAyahNumber, startIndex: $calculatedStart")
                            calculatedStart
                        } else {
                            Log.w("JuzDetail", "Target Surah $targetSurahNumber, Ayah $targetAyahNumber not found, loading normally")
                            currentPage * pageSize
                        }
                    } else {
                        currentPage * pageSize
                    }

                    val endIndex = minOf(startIndex + pageSize, totalAyahs)
                    val ayahList = mutableListOf<AyahEdition>()

                    ayahs.subList(startIndex, endIndex).forEach { ayah ->
                        val reference = "${ayah.surah.number}:${ayah.numberInSurah}"
                        val editionResponse = repository.getAyahEditions(reference, editions)
                        if (editionResponse.code == 200) {
                            ayahList.addAll(editionResponse.data)
                        } else {
                            Log.w("JuzDetail", "Failed to load editions for $reference: ${editionResponse.status}")
                        }
                    }
                    if (reset || (targetSurahNumber != null && targetAyahNumber != null)) {
                        _juzDetail.value = ayahList
                    } else {
                        _juzDetail.value = _juzDetail.value + ayahList
                    }
                    currentPage++
                    Log.d("JuzDetail", "Loaded ayahs from $startIndex to $endIndex, current size: ${_juzDetail.value.size}")
                } else {
                    _error.value = "Failed to load Juz $juzNumber: ${response.status}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("JuzDetail", "Exception: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun hasMoreAyahs() = (currentPage * pageSize) < totalAyahs
}
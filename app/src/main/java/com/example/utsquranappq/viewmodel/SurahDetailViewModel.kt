package com.example.utsquranappq.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.utsquranappq.model.AyahEdition
import com.example.utsquranappq.repository.QuranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SurahDetailViewModel : ViewModel() {
    private val repository = QuranRepository()

    private val _surahDetail = MutableStateFlow<List<AyahEdition>>(emptyList())
    val surahDetail: StateFlow<List<AyahEdition>> = _surahDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedQari = MutableStateFlow<String?>(null)
    val selectedQari: StateFlow<String?> = _selectedQari

    private var currentPage = 0
    private val pageSize = 7 // Memuat 10 ayat per halaman
    private var totalAyahs = 0
    private var surahNumberLoaded = -1

    fun fetchSurahDetail(surahNumber: Int, reset: Boolean = false) {
        if (reset || surahNumber != surahNumberLoaded) {
            currentPage = 0
            _surahDetail.value = emptyList()
            surahNumberLoaded = surahNumber
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val editions = "quran-tajweed,en.transliteration,id.indonesian,ar.alafasy,ar.abdurrahmaansudais,ar.husary,ar.minshawi,ar.ahmedajamy"
                val response = repository.getSurah(surahNumber)
                if (response.code == 200) {
                    val ayahs = response.data.ayahs
                    totalAyahs = ayahs.size
                    val startIndex = currentPage * pageSize
                    val endIndex = minOf(startIndex + pageSize, totalAyahs)
                    val ayahList = mutableListOf<AyahEdition>()

                    ayahs.subList(startIndex, endIndex).forEach { ayah ->
                        val reference = "$surahNumber:${ayah.numberInSurah}"
                        val editionResponse = repository.getAyahEditions(reference, editions)
                        if (editionResponse.code == 200) {
                            ayahList.addAll(editionResponse.data)
                        }
                    }
                    _surahDetail.value = _surahDetail.value + ayahList
                    currentPage++
                } else {
                    _error.value = "Failed to load Surah $surahNumber: ${response.status}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectQari(qari: String?) {
        _selectedQari.value = qari
    }

    fun hasMoreAyahs() = (currentPage * pageSize) < totalAyahs
}
package com.example.utsquranappq.viewmodel

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

    fun fetchJuzDetail(juzNumber: Int, reset: Boolean = false) {
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
                    val startIndex = currentPage * pageSize
                    val endIndex = minOf(startIndex + pageSize, totalAyahs)
                    val ayahList = mutableListOf<AyahEdition>()

                    ayahs.subList(startIndex, endIndex).forEach { ayah ->
                        val reference = "${ayah.surah.number}:${ayah.numberInSurah}"
                        val editionResponse = repository.getAyahEditions(reference, editions)
                        if (editionResponse.code == 200) {
                            ayahList.addAll(editionResponse.data)
                        }
                    }
                    _juzDetail.value = _juzDetail.value + ayahList
                    currentPage++
                } else {
                    _error.value = "Failed to load Juz $juzNumber: ${response.status}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun hasMoreAyahs() = (currentPage * pageSize) < totalAyahs
}
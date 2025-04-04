package com.example.utsquranappq.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.utsquranappq.model.AyahEdition
import com.example.utsquranappq.repository.QuranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class SurahDetailViewModel : ViewModel() {
    private val repository = QuranRepository()

    private val _surahDetail = MutableStateFlow<List<AyahEdition>>(emptyList())
    val surahDetail: StateFlow<List<AyahEdition>> = _surahDetail

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchSurahDetail(surahNumber: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Tambahkan ar.alafasy ke daftar edisi
                val editions = "quran-tajweed,en.transliteration,id.indonesian,ar.alafasy,ar.abdurrahmaansudais,ar.husary,ar.minshawi,ar.ahmedajamy"
                val ayahList = mutableListOf<AyahEdition>()
                val surahResponse = repository.getSurah(surahNumber)
                if (surahResponse.code == 200) {
                    val ayahs = surahResponse.data.ayahs
                    ayahs.forEach { ayah ->
                        val reference = "$surahNumber:${ayah.numberInSurah}"
                        val response = repository.getAyahEditions(reference, editions)
                        if (response.code == 200) {
                            ayahList.addAll(response.data) // Semua edisi (termasuk audio) ditambahkan
                        }
                    }
                    _surahDetail.value = ayahList
                    Log.d("SurahDetailViewModel", "Berhasil mengambil ${ayahList.size} edisi ayat")
                } else {
                    _error.value = "Gagal memuat surah: ${surahResponse.status}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("SurahDetailViewModel", "Exception: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
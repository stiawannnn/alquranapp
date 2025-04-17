package com.example.quranappbystiawan.repository

import android.util.Log
import com.example.quranappbystiawan.model.AyahEditionResponse
import com.example.quranappbystiawan.model.JuzResponse
import com.example.quranappbystiawan.model.SurahDetailResponse
import com.example.quranappbystiawan.model.SurahResponse
import com.example.quranappbystiawan.network.RetrofitInstance

class QuranRepository {
    private val api = RetrofitInstance.api
      //daftar surah
    suspend fun getSurahList(): SurahResponse {
        return api.getSurahList()
    }

    // Mengambil detail surah berdasarkan nomor surah
    suspend fun getSurah(surahNumber: Int): SurahDetailResponse {
        val response = api.getSurah(surahNumber)
        return if (response.isSuccessful) {
            response.body() ?: throw Exception("No data")
        } else {
            throw Exception("API Error: ${response.code()} - ${response.message()}")
        }
    }
        // Mengambil ayat dengan beberapa edisi (Arab utsmani, Latin, Indonesia)
        suspend fun getAyahEditions(reference: String, editions: String): AyahEditionResponse {
            val response = api.getAyahEditions(reference, editions)
            return if (response.isSuccessful) {
                response.body() ?: throw Exception("No data")
            } else {
                throw Exception("API Error: ${response.code()} - ${response.message()}")

            }
        }

    suspend fun getJuz(juzNumber: Int): JuzResponse {
        return try {
            val response = api.getJuz(juzNumber)
            Log.d("QuranRepository", "Juz $juzNumber fetched: ${response.data.ayahs.size} ayahs")
            response
        } catch (e: Exception) {
            Log.e("QuranRepository", "Error fetching juz $juzNumber: ${e.message}", e)
            throw e
        }
    }
}


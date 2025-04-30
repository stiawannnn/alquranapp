package app.dev.quranappbystiawan.repository

import android.util.Log
import app.dev.quranappbystiawan.model.AyahEditionResponse
import app.dev.quranappbystiawan.model.JuzResponse
import app.dev.quranappbystiawan.model.SurahDetailResponse
import app.dev.quranappbystiawan.model.SurahResponse
import app.dev.quranappbystiawan.network.RetrofitInstance

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


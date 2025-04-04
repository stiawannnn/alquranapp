package com.example.utsquranappq.repository

import com.example.utsquranappq.model.AyahEditionResponse
import com.example.utsquranappq.model.SurahDetailResponse
import com.example.utsquranappq.model.SurahResponse
import com.example.utsquranappq.network.RetrofitInstance

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
}

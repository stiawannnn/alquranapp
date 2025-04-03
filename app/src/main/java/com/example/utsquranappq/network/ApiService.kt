package com.example.quranapp.data.network

import com.example.utsquranappq.model.SurahResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("surah")
    suspend fun getSurahList(): SurahResponse

}


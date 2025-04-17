package com.example.quranappbystiawan.network

import com.example.quranappbystiawan.model.AyahEditionResponse
import com.example.quranappbystiawan.model.JuzResponse
import com.example.quranappbystiawan.model.SurahDetailResponse
import com.example.quranappbystiawan.model.SurahResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("surah")
    suspend fun getSurahList(): SurahResponse

    @GET("surah/{surahNumber}")
    suspend fun getSurah(
        @Path("surahNumber") surahNumber: Int
    ): Response<SurahDetailResponse>

    @GET("ayah/{reference}/editions/{editions}")
    suspend fun getAyahEditions(
        @Path("reference") reference: String,
        @Path("editions") editions: String
    ): Response<AyahEditionResponse>


    @GET("juz/{juzNumber}/quran-tajweed")
    suspend fun getJuz(@Path("juzNumber") juzNumber: Int): JuzResponse
}




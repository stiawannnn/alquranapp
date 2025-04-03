package com.example.utsquranappq.repository

import com.example.utsquranappq.model.SurahResponse
import com.example.utsquranappq.network.RetrofitInstance

class SurahRepository {
    private val api = RetrofitInstance.api

    suspend fun getSurahList(): SurahResponse {
        return api.getSurahList()

    }
}

package com.example.myapplicationp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("hamburguesas")
    suspend fun getHamburguesas(): List<Burger>

    companion object {
        private const val BASE_URL = "https://6ae4f09328393034b08e0b02.mockapi.io/api/v1/"

        val instance: ApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}

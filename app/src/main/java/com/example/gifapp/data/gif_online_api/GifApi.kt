package com.example.gifapp.data.gif_online_api

import com.example.gifapp.data.gif_online_api.entities.GifResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GifApi {
    @GET("v1/gifs/trending")
    suspend fun getGifs(
        @Query("api_key") api_key: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): GifResponse

    @GET("v1/gifs/search")
    suspend fun searchGifs(
        @Query("api_key") api_key: String,
        @Query("q") search_string: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): GifResponse
}
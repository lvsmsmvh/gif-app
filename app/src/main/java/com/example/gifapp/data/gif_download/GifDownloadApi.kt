package com.example.gifapp.data.gif_download

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface GifDownloadApi {
    @Streaming
    @GET
    suspend fun downloadFile(@Url url: String): Response<ResponseBody>
}
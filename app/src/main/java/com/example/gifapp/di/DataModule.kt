package com.example.gifapp.di

import android.app.Application
import androidx.room.Room
import com.example.gifapp.data.gif_online_api.GifApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    companion object {
        private const val GIF_API_URL = "https://api.giphy.com/"
    }

    private class RetrofitBuilder<API_TYPE>(
        private val apiClass: Class<API_TYPE>,
        private val baseUrl: String
    ) {
        fun build(): API_TYPE =
            Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build()
                .create(apiClass)
    }

    @Provides
    @Singleton
    fun provideGifApi(): GifApi = RetrofitBuilder(
        GifApi::class.java,
        GIF_API_URL
    ).build()

}
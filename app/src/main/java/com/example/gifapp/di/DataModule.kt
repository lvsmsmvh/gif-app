package com.example.gifapp.di

import android.app.Application
import androidx.room.Room
import com.example.gifapp.data.gif_db_room.DATABASE_NAME
import com.example.gifapp.data.gif_db_room.RoomDB
import com.example.gifapp.data.gif_online_api.GifApi
import com.example.gifapp.data.repository.LocalRoomToMediaStoreImpl
import com.example.gifapp.data.repository.OnlineGiphyImpl
import com.example.gifapp.data.repository.RemovedGifsRoomImpl
import com.example.gifapp.domain.reposities.LocalGifRepository
import com.example.gifapp.domain.reposities.OnlineGifRepository
import com.example.gifapp.domain.reposities.RemovedGifsRepository
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

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application) = Room
        .databaseBuilder(application, RoomDB::class.java, DATABASE_NAME)
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideGifDao(appDatabase: RoomDB) = appDatabase.gifDao()

    @Provides
    fun provideRemovedGifsDao(appDatabase: RoomDB) = appDatabase.removedGifsDao()

    @Provides
    @Singleton
    fun provideOnlineGifRepository(
        onlineGifRepository: OnlineGiphyImpl
    ): OnlineGifRepository = onlineGifRepository

    @Provides
    @Singleton
    fun provideLocalGifRepository(
        localGifRepository: LocalRoomToMediaStoreImpl
    ): LocalGifRepository = localGifRepository

    @Provides
    @Singleton
    fun provideRemovedGifsRepository(
        removedGifsImpl: RemovedGifsRoomImpl
    ): RemovedGifsRepository = removedGifsImpl
}
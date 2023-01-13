package com.example.gifapp.data.repository

import android.content.Context
import com.example.gifapp.data.gif_download.GifDownloadApi
import com.example.gifapp.domain.entities.GifPicture
import com.example.gifapp.domain.exceptions.LoadException
import com.example.gifapp.domain.reposities.GifSourceRepository
import com.example.gifapp.domain.reposities.LocalGifRepository
import com.example.gifapp.utils.MediaSaverUtil
import com.example.gifapp.utils.logDebug
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlin.io.path.Path

class GifSourceImpl @Inject constructor(
    private val context: Context,
    private val gifDownloadApi: GifDownloadApi,
    private val localGifRepository: LocalGifRepository,
) : GifSourceRepository {

    private data class LoadingGifJob(val gifId: String, val job: Job)

    private val loadingGifJobs = mutableListOf<LoadingGifJob>()

    override suspend fun downloadAndGetLocalUrl(gifPicture: GifPicture): Result<String> {
        getLocalUrl(gifPicture)?.let {
            return Result.success(it)
        }

        val loadingGifJobsCopy = loadingGifJobs.toList()
        val loadingJob = loadingGifJobsCopy.find { it.gifId == gifPicture.id && it.job.isActive }?.job
            ?: createLoadingJob(gifPicture)

        val loadingGifJob = LoadingGifJob(gifPicture.id, loadingJob)

        loadingGifJobs.add(loadingGifJob)
        loadingJob.join()
        loadingGifJobs.remove(loadingGifJob)

        getLocalUrl(gifPicture)?.let { url ->
            return Result.success(url)
        }

        return Result.failure(LoadException("Failed to load image from web server."))
    }

    private fun getLocalUrl(gifPicture: GifPicture): String? {
        val localUrl = localGifRepository.getLocalUrl(gifPicture) ?: return null

        return when (File(localUrl).exists()) {
            true -> localUrl
            false -> null
        }
    }

    private fun createLoadingJob(gifPicture: GifPicture): Job {
        val job = CoroutineScope(Dispatchers.IO).launch {
            val body = try {
                gifDownloadApi.downloadFile(gifPicture.url).body()
            } catch (e: IOException) {
                cancel(e.message.toString())
                return@launch
            } catch (e: RuntimeException) {
                cancel(e.message.toString())
                return@launch
            }

            body?.let { responseBody ->
                val byteArray = responseBody.bytes()
                val localCopyUrl = MediaSaverUtil.saveGif(context, byteArray, gifPicture)
                localGifRepository.saveLocalUrl(gifPicture, localCopyUrl)
            }
        }
        return job
    }
}
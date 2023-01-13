package com.example.gifapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gifapp.domain.entities.GifPicture
import com.example.gifapp.domain.entities.Page
import com.example.gifapp.domain.usecases.base.LoadPageUseCase
import com.example.gifapp.domain.usecases.local.LoadPageLocalUseCase
import com.example.gifapp.domain.usecases.local.RemoveGifLocalUseCase
import com.example.gifapp.domain.usecases.online.LoadPageOnlineUseCase
import com.example.gifapp.domain.usecases.other.DownloadAndGetLocalUrlUseCase
import com.example.gifapp.utils.logDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class PageViewModel @Inject constructor(
    private val loadPageOnlineUseCase: LoadPageOnlineUseCase,
    private val loadPageLocalUseCase: LoadPageLocalUseCase,
    private val downloadAndGetLocalUrlUseCase: DownloadAndGetLocalUrlUseCase,
    private val removeGifLocalUseCase: RemoveGifLocalUseCase,
) : BaseViewModel() {

    companion object {
        private const val FIRST_PAGE_INDEX = 1
    }

    private enum class NetworkMode { ONLINE, OFFLINE }
    private data class PageLoadAttempt(val pageIndex: Int, val query: String)

    private var networkMode: NetworkMode = NetworkMode.ONLINE
    private var pageLoadAttempt: PageLoadAttempt? = null
    private var loadingPageJob: Job? = null
    private var loadingImagesJob: Job? = null


    val page: LiveData<LoadingState<Page>> = MutableLiveData()
    val localUrls: LiveData<Map<GifPicture, LiveData<LoadingState<String>>>> = MutableLiveData()



    fun removeGif(gifPicture: GifPicture) {
        removeGifFromLoadedPage(gifPicture)
        removeGifFromStorage(gifPicture)
    }

    private fun removeGifFromStorage(gifPicture: GifPicture) {
        makeSimpleRequest {
            removeGifLocalUseCase(gifPicture)
        }
    }

    private fun removeGifFromLoadedPage(gifPicture: GifPicture) {
        loadedPageOrNull()?.let {
            val pictures = it.gifPictures
            if (!pictures.contains(gifPicture)) return
            val mutablePictures = pictures.toMutableList()
            mutablePictures.remove(gifPicture)
            val newPage = it.copy(gifPictures = mutablePictures)
            page.postValue(LoadingState.Loaded(newPage))
        }
    }

    fun updateFailedPage() {
        pageLoadAttempt?.let {
            loadPage(it.pageIndex, it.query)
        }
    }

    fun loadFirstPageIfNothingLoaded() {
        loadedPageOrNull() ?: loadPage(FIRST_PAGE_INDEX, "")
    }

    fun search(query: String) {
        loadedPageOrNull()?.let {
            val sameQuery = it.query == query
            if (sameQuery) return
        }

        loadPage(FIRST_PAGE_INDEX, query)
    }

    fun loadNextPage() {
        updatePageWithIndexChange(1)
    }

    fun loadPreviousPage() {
        updatePageWithIndexChange(-1)
    }

    private fun updatePageWithIndexChange(changeIndex: Int) {
        loadedPageOrNull()?.let { currentPage ->
            loadPage(currentPage.pageNumber + changeIndex, currentPage.query)
        }
    }

    private fun loadPage(pageIndex: Int, query: String) {
        logDebug("loadPage $pageIndex")
        loadedPageOrNull()?.let { currentPage ->
            val samePage = currentPage.pageNumber == pageIndex && currentPage.query == query
            if (samePage) {
                return
            }
        }

        loadPageEvenIfLoaded(pageIndex, query)
    }

    private fun loadedPageOrNull(): Page? {
        return page.value?.asLoaded()?.result
    }

    private fun loadPageEvenIfLoaded(pageIndex: Int, query: String) {
        logDebug("loadPageEvenIfLoaded $pageIndex")
        pageLoadAttempt = PageLoadAttempt(pageIndex, query)

        val useCase: LoadPageUseCase = when (networkMode) {
            NetworkMode.ONLINE -> loadPageOnlineUseCase
            NetworkMode.OFFLINE -> loadPageLocalUseCase
        }

        loadingPageJob?.cancel()
        loadingPageJob = makeLoadingRequest(page) {
            useCase(pageIndex, query)
        }
    }

    fun loadImages(gifPictures: List<GifPicture>) {
        loadingImagesJob?.cancel()
        loadingImagesJob = viewModelScope.launch(Dispatchers.IO) {
            gifPictures.associateWith {
                MutableLiveData<LoadingState<String>>().apply { postValue(LoadingState.Loading) }
            }.apply { localUrls.postValue(this) }.forEach { (gifPicture, liveData) ->
                launch {
                    downloadAndGetLocalUrlUseCase(gifPicture)
                        .onSuccess { liveData.postValue(LoadingState.Loaded(it)) }
                        .onFailure { liveData.postValue(LoadingState.Failed(it)) }
                }
            }
        }
    }

    fun goOfflineMode() {
        changeModeTo(NetworkMode.OFFLINE)
    }

    fun goOnlineMode() {
        changeModeTo(NetworkMode.ONLINE)
    }

    private fun changeModeTo(networkMode: NetworkMode) {
        logDebug("tablayout Change mode to ${networkMode.name}")
        if (this.networkMode == networkMode) return
        this.networkMode = networkMode
        loadPageEvenIfLoaded(FIRST_PAGE_INDEX, "")
    }
}
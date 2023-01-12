package com.example.gifapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gifapp.domain.entities.GifPicture
import com.example.gifapp.domain.entities.Page
import com.example.gifapp.domain.usecases.base.LoadPageUseCase
import com.example.gifapp.domain.usecases.local.LoadPageLocalUseCase
import com.example.gifapp.domain.usecases.online.LoadPageOnlineUseCase
import com.example.gifapp.domain.usecases.other.DownloadAndGetLocalUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class PageViewModel @Inject constructor(
    private val loadPageOnlineUseCase: LoadPageOnlineUseCase,
    private val loadPageLocalUseCase: LoadPageLocalUseCase,
    private val downloadAndGetLocalUrlUseCase: DownloadAndGetLocalUrlUseCase,
) : BaseViewModel() {

    companion object {
        private const val FIRST_PAGE_INDEX = 1
    }

    private enum class CurrentMode { ONLINE, OFFLINE }

    private var currentMode: CurrentMode = CurrentMode.ONLINE

    private var loadingPageJob: Job? = null
    private var loadingImagesJob: Job? = null

    private val loadedLocalUrls = mutableMapOf<GifPicture, LoadingState<String>>()

    val page: LiveData<LoadingState<Page>> = MutableLiveData()
//    val localUrlsOld: LiveData<Map<GifPicture, LoadingState<String>>> = MutableLiveData()

    val localUrls: LiveData<Map<GifPicture, LiveData<LoadingState<String>>>> = MutableLiveData()

//    val loadedLocalUrls: LiveData<Map<GifPicture, LoadingState<String>>> = transform(localUrls) {
//
//    }

    fun loadFirstPage() {
        loadPage(FIRST_PAGE_INDEX, "")
    }

    fun search(query: String) {
        loadPage(FIRST_PAGE_INDEX, query)
    }

    fun loadPage(pageIndex: Int, query: String) {
        val useCase: LoadPageUseCase = when (currentMode) {
            CurrentMode.ONLINE -> loadPageOnlineUseCase
            CurrentMode.OFFLINE -> loadPageLocalUseCase
        }

        loadingPageJob?.cancel()
        loadingPageJob = makeLoadingRequest(page) {
            useCase(pageIndex, query)
        }
    }

    fun loadImages(gifPictures: List<GifPicture>) {
        loadingImagesJob?.cancel()
        loadingImagesJob = viewModelScope.launch(Dispatchers.IO) {

//            loadedLocalUrls.clear()

            gifPictures.associateWith {
                MutableLiveData<LoadingState<String>>().apply { postValue(LoadingState.Loading) }
            }.apply { localUrls.postValue(this) }.forEach { (gifPicture, liveData) ->
                launch {
                    downloadAndGetLocalUrlUseCase(gifPicture)
                        .onSuccess { liveData.postValue(LoadingState.Loaded(it)) }
                        .onFailure { liveData.postValue(LoadingState.Failed(it)) }
                }
            }

//            launch {
//                fun putNewState(loadingState: LoadingState<String>) {
//                    loadedLocalUrls[gifPicture] = loadingState
//                    localUrlsOld.postValue(loadedLocalUrls)
//                }
//
//                downloadAndGetLocalUrlUseCase(gifPicture)
//                    .onSuccess { putNewState(LoadingState.Loaded(it)) }
//                    .onFailure { putNewState(LoadingState.Failed(it)) }
//            }
//
//            gifPictures.forEach { gifPicture ->
//                launch {
//                    fun putNewState(loadingState: LoadingState<String>) {
//                        loadedLocalUrls[gifPicture] = loadingState
//                        localUrlsOld.postValue(loadedLocalUrls)
//                    }
//
//                    downloadAndGetLocalUrlUseCase(gifPicture)
//                        .onSuccess { putNewState(LoadingState.Loaded(it)) }
//                        .onFailure { putNewState(LoadingState.Failed(it)) }
//                }
//            }
        }
    }

    fun goOfflineMode() {
        currentMode = CurrentMode.OFFLINE
        loadFirstPage()
    }

    fun goOnlineMode() {
        currentMode = CurrentMode.ONLINE
        loadFirstPage()
    }
}
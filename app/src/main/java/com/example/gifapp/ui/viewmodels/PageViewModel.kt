package com.example.gifapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gifapp.domain.common.FIRST_PAGE_INDEX
import com.example.gifapp.domain.entities.GifPicture
import com.example.gifapp.domain.entities.Page
import com.example.gifapp.domain.usecases.base.LoadPageUseCase
import com.example.gifapp.domain.usecases.local.LoadPageLocalUseCase
import com.example.gifapp.domain.usecases.local.RemoveGifLocalUseCase
import com.example.gifapp.domain.usecases.online.LoadPageOnlineUseCase
import com.example.gifapp.domain.usecases.other.DownloadAndGetLocalUrlUseCase
import com.example.gifapp.ui.entities.NetworkMode
import com.example.gifapp.ui.entities.PageLoadAttempt
import com.example.gifapp.ui.entities.PaginationModel
import com.example.gifapp.ui.entities.ValueChange
import com.example.gifapp.utils.transform
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

    private var networkMode: NetworkMode = NetworkMode.ONLINE
    private var pageLoadAttempt: PageLoadAttempt? = null
    private var loadingPageJob: Job? = null
    private var loadingImagesJob: Job? = null

    val localUrls: LiveData<Map<GifPicture, LiveData<LoadingState<String>>>> = MutableLiveData()
    val page: LiveData<LoadingState<Page>> = MutableLiveData()
    val pagination: LiveData<PaginationModel> = transform(page) {
        it.asLoaded()?.result?.let { page ->
            return@transform PaginationModel(
                hideButtons = false,
                isPrevEnabled = ValueChange.New(page.pageNumber != 1),
                isNextEnabled = ValueChange.New(page.pageNumber != page.pagesAmount),
                indicator = ValueChange.New("" + page.pageNumber + "/" + page.pagesAmount)
            )
        }

        return@transform PaginationModel(hideButtons = it.isLoading())
    }

    fun goOfflineMode() {
        changeModeTo(NetworkMode.OFFLINE)
    }

    fun goOnlineMode() {
        changeModeTo(NetworkMode.ONLINE)
    }

    fun updateFailedPage() {
        pageLoadAttempt?.let {
            loadPage(it.pageIndex, it.query)
        }
    }

    fun loadFirstPageIfNothingLoaded() {
        getLoadedPageOrNull() ?: loadPage(FIRST_PAGE_INDEX, "")
    }

    fun loadNextPage() {
        updatePageWithIndexChange(1)
    }

    fun loadPreviousPage() {
        updatePageWithIndexChange(-1)
    }

    fun search(query: String) {
        getLoadedPageOrNull()?.let {
            val sameQuery = it.query == query
            if (sameQuery) return
        }

        loadPage(FIRST_PAGE_INDEX, query)
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
    fun removeGif(gifPicture: GifPicture) {
        // update loaded page
        getLoadedPageOrNull()?.let {
            val pictures = it.gifPictures
            if (!pictures.contains(gifPicture)) return
            val mutablePictures = pictures.toMutableList()
            mutablePictures.remove(gifPicture)
            val newPage = it.copy(gifPictures = mutablePictures)
            page.postValue(LoadingState.Loaded(newPage))
        }

        // delete from storage
        makeSimpleRequest {
            removeGifLocalUseCase(gifPicture)
        }
    }

    private fun changeModeTo(networkMode: NetworkMode) {
        if (this.networkMode == networkMode) return
        this.networkMode = networkMode
        loadPageEvenIfLoaded(FIRST_PAGE_INDEX, "")
    }

    private fun updatePageWithIndexChange(changeIndex: Int) {
        getLoadedPageOrNull()?.let { currentPage ->
            loadPage(currentPage.pageNumber + changeIndex, currentPage.query)
        }
    }

    private fun loadPage(pageIndex: Int, query: String) {
        getLoadedPageOrNull()?.let { currentPage ->
            val samePage = currentPage.pageNumber == pageIndex && currentPage.query == query
            if (samePage) {
                return
            }
        }

        loadPageEvenIfLoaded(pageIndex, query)
    }

    private fun getLoadedPageOrNull(): Page? {
        return page.value?.asLoaded()?.result
    }

    private fun loadPageEvenIfLoaded(pageIndex: Int, query: String) {
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
}
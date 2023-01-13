package com.example.gifapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlin.math.max

open class BaseViewModel : ViewModel() {

    companion object {
        /**
         * MIN_EXECUTION_TIME_MS is needed to show a visual part of a retry process,
         * so the user will see that reloading (for example, of a page)
         * actually happened but not the app just got frozen instead.
         */
        private const val MIN_EXECUTION_TIME_MS = 500L
    }

    /**
     * We need this jobs to cancel them in 'onCleared()',
     * so all of them will stop their work and they will not change
     * the live data anymore after 'onCleared()' is called.
     */

    private var jobs = mutableListOf<Job>()

    private fun createJob() = Job().apply { jobs.add(this) }

    override fun onCleared() {
        super.onCleared()
        jobs.forEach { it.cancel() }
        jobs.clear()
    }

    protected fun <T> LiveData<T>.postValue(value: T?) {
        (this as? MutableLiveData)?.postValue(value)
    }

    /**
     * Delay if we get an error and a response time was very small, because a user
     * might get an idea that the app has not reacted on his click.
     */

    protected fun <T : Any> makeLoadingRequest(
        liveData: LiveData<LoadingState<T>>,
        allowInterrupt: Boolean = true,
        source: suspend (() -> Result<T>),
    ): Job {
        val job = createJob()
        viewModelScope.launch(job + Dispatchers.IO) {
            if (liveData.value.isLoading() && !allowInterrupt) return@launch
            liveData.postValue(LoadingState.Loading)
            val startTime = System.currentTimeMillis()
            val response = source()
            yield()
            if (response.isFailure) {
                val executionTime = System.currentTimeMillis() - startTime
                delayIfExecutionTimeIsSmall(executionTime)
            }
            liveData.postValue(LoadingState.fromResult(response))
        }
        return job
    }

    protected fun makeSimpleRequest(source: suspend (() -> Unit)) {
        val job = createJob()
        viewModelScope.launch(job + Dispatchers.IO) {
            source()
        }
    }

    private suspend fun delayIfExecutionTimeIsSmall(executionTime: Long) {
        delay(max(MIN_EXECUTION_TIME_MS - executionTime, 0L))
    }
}
package com.example.gifapp.ui.viewmodels

sealed class LoadingState<out T : Any> {
    object Loading : LoadingState<Nothing>()
    data class Loaded<out T : Any>(val result: T) : LoadingState<T>()
    data class Failed(val throwable: Throwable) : LoadingState<Nothing>()

    companion object {
        fun <T : Any> fromResult(result: Result<T>): LoadingState<T> {
            return result.getOrNull()?.let {
                Loaded(it)
            } ?: result.exceptionOrNull()?.let {
                Failed(it)
            } ?: throw Exception("kotlin.Result has unknown value")
        }
    }
}

fun <T : Any> LoadingState<T>?.asLoaded() = this as? LoadingState.Loaded
fun <T : Any> LoadingState<T>?.asFailed() = this as? LoadingState.Failed

fun <T : Any> LoadingState<T>?.isLoading() = this is LoadingState.Loading
fun <T : Any> LoadingState<T>?.isFailed() = this is LoadingState.Failed
fun <T : Any> LoadingState<T>?.isLoaded() = this is LoadingState.Loaded
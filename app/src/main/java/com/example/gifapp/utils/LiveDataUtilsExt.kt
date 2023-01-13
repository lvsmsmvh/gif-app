package com.example.gifapp.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun <A, B> transform(
    source: LiveData<A>,
    mapper: (A) -> B,
): LiveData<B> {
    val mediatorLiveData = MediatorLiveData<B>()

    mediatorLiveData.addSource(source) { aValue ->
        mapper(aValue)?.let { mediatorLiveData.postValue(it) }
    }

    return mediatorLiveData
}

fun <A, B> transformWithDefault(
    source: LiveData<A>,
    defaultValue: B,
    mapper: (A) -> B?,
): LiveData<B> {

    val mediatorLiveData = MediatorLiveData<B>()
    mediatorLiveData.value = defaultValue!!

    mediatorLiveData.addSource(source) { aValue ->
        mapper(aValue)?.let { mediatorLiveData.postValue(it) }
    }

    return mediatorLiveData
}

fun <A, B> trigger(
    source: LiveData<A>,
    mapper: (A) -> Unit,
): LiveData<B> {
    val mediatorLiveData = MediatorLiveData<B>()

    mediatorLiveData.addSource(source) { aValue ->
        mapper(aValue)
    }

    return mediatorLiveData
}

fun <A, B, C> transformWithDoubleTrigger(
    firstSource: LiveData<A>,
    secondSource: LiveData<B>,
    mapper: (A?, B?) -> C,
): LiveData<C> {

    val mediatorLiveData = MediatorLiveData<C>()

    mediatorLiveData.addSource(firstSource) {
        mediatorLiveData.value = mapper(it, secondSource.value)
    }
    mediatorLiveData.addSource(secondSource) {
        mediatorLiveData.value = mapper(firstSource.value, it)
    }
    return mediatorLiveData
}
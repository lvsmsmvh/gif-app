package com.example.gifapp.ui.entities

sealed class ValueChange<out T : Any> {
    object Previous : ValueChange<Nothing>()
    data class New<out T: Any>(val value: T) : ValueChange<T>()

    fun asNewOrNull() = this as? New<T>
}
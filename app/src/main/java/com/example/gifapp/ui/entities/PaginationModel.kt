package com.example.gifapp.ui.entities

data class PaginationModel(
    val hideButtons: Boolean,
    val isPrevEnabled: ValueChange<Boolean> = ValueChange.Previous,
    val isNextEnabled: ValueChange<Boolean> = ValueChange.Previous,
    val indicator: ValueChange<String> = ValueChange.Previous,
)
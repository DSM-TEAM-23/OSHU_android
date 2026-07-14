package com.example.oshu_android.feature.inquiry

data class InquiryUiState(
    val title: String = "",
    val name: String = "",
    val number: String = "",
    val content: String = "",
    val isSubmitting: Boolean = false,
    val submitted: Boolean = false,
    val errorMessage: String? = null,
)

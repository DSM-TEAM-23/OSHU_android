package com.example.oshu_android.data.inquiry

data class InquiryRequest(
    val title: String,
    val content: String,
    val name: String,
    val number: String,
)

data class InquiryResponse(
    val id: Long = 0L,
    val title: String = "",
    val content: String = "",
    val name: String = "",
    val number: String = "",
)

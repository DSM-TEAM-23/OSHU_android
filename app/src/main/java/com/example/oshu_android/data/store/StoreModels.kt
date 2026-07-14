package com.example.oshu_android.data.store

data class StoreCardResponse(
    val storeId: Long = 0L,
    val name: String = "",
    val category: String = "",
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val crowdLevel: String? = null,
    val timeSaleActive: Boolean = false,
    val externalData: Boolean = false,
)

data class PageResponse<T>(
    val content: List<T> = emptyList(),
    val page: Int = 0,
    val size: Int = 0,
    val totalElements: Long = 0L,
    val totalPages: Int = 0,
)

data class PromotionResponse(
    val promotionId: Long = 0L,
    val storeId: Long = 0L,
    val storeName: String = "",
    val type: String = "",
    val title: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    val startAt: String? = null,
    val endAt: String? = null,
    val status: String = "",
)
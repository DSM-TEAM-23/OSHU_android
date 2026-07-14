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

data class StoreDetailResponse(
    val storeId: Long = 0L,
    val name: String = "",
    val category: String = "",
    val description: String? = null,
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val phone: String? = null,
    val openingHours: String? = null,
    val crowdStatus: CrowdStatusResponse? = null,
    val promotions: List<PromotionResponse> = emptyList(),
    val timeSales: List<TimeSaleResponse> = emptyList(),
)

data class StoreCreateRequest(
    val name: String,
    val category: String,
    val description: String? = null,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String? = null,
    val openingHours: String? = null,
)

data class StoreUpdateRequest(
    val description: String? = null,
    val phone: String? = null,
    val openingHours: String? = null,
)

data class CrowdStatusRequest(
    val level: CrowdLevel,
    val estimatedWaitingMinutes: Int,
)

enum class CrowdLevel {
    RELAXED,
    NORMAL,
    BUSY,
    VERY_BUSY,
}

data class CrowdStatusResponse(
    val level: String = "",
    val label: String = "",
    val estimatedWaitingMinutes: Int = 0,
)

data class TimeSaleRequest(
    val productName: String,
    val originalPrice: Int,
    val salePrice: Int,
    /** ISO-8601 date-time, for example 2026-07-14T09:00:00. */
    val startAt: String,
    /** ISO-8601 date-time, for example 2026-07-14T12:00:00. */
    val endAt: String,
    val notice: String? = null,
)

data class TimeSaleResponse(
    val timeSaleId: Long = 0L,
    val storeId: Long = 0L,
    val productName: String = "",
    val originalPrice: Int = 0,
    val salePrice: Int = 0,
    val startAt: String? = null,
    val endAt: String? = null,
    val notice: String? = null,
    val status: String = "",
)

data class PromotionRequest(
    val type: String,
    val title: String,
    val content: String? = null,
    val imageUrl: String? = null,
    /** ISO-8601 date-time, for example 2026-07-14T09:00:00. */
    val startAt: String,
    /** ISO-8601 date-time, for example 2026-07-20T23:59:59. */
    val endAt: String,
)

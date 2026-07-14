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

data class StorePageResponse(
    val content: List<StoreCardResponse> = emptyList(),
    val page: Int = 0,
    val size: Int = 0,
    val totalElements: Long = 0L,
)

data class CrowdStatusResponse(
    val level: String? = null,
    val label: String? = null,
    val estimatedWaitingMinutes: Int? = null,
)

data class PromotionResponse(
    val promotionId: Long = 0L,
    val storeId: Long = 0L,
    val storeName: String = "",
    val type: String = "",
    val title: String = "",
    val content: String? = null,
    val imageUrl: String? = null,
    val startAt: String? = null,
    val endAt: String? = null,
    val status: String? = null,
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
    val status: String? = null,
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
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
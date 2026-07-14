package com.example.oshu_android.data.store

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StoreApi {
    @GET("stores/map")
    suspend fun getMapStores(
        @Query("latitude")
        latitude: Double,
        @Query("longitude")
        longitude: Double,
        @Query("radius")
        radius: Int,
        @Query("timeSaleOnly")
        timeSaleOnly: Boolean,
    ): Response<List<StoreCardResponse>>

    @GET("promotions")
    suspend fun getPromotions(
        @Query("status")
        status: String? = null,
        @Query("page")
        page: Int = 0,
        @Query("size")
        size: Int = 50,
    ): Response<PageResponse<PromotionResponse>>
}
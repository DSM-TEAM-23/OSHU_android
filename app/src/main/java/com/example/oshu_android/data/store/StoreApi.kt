package com.example.oshu_android.data.store

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StoreApi {

    @GET("stores/map")
    suspend fun getMapStores(
        @Query("latitude")
        latitude: Double,
        @Query("longitude")
        longitude: Double,
        @Query("radius")
        radius: Int = 1500,
        @Query("timeSaleOnly")
        timeSaleOnly: Boolean = false,
    ): List<StoreCardResponse>

    @GET("stores")
    suspend fun getStores(
        @Query("keyword")
        keyword: String? = null,
        @Query("category")
        category: String? = null,
        @Query("page")
        page: Int = 0,
        @Query("size")
        size: Int = 20,
    ): StorePageResponse

    @GET("stores/{storeId}")
    suspend fun getStoreDetail(
        @Path("storeId")
        storeId: Long,
    ): StoreDetailResponse

    @GET("stores/{storeId}/summary")
    suspend fun getStoreSummary(
        @Path("storeId")
        storeId: Long,
    ): StoreCardResponse

    @GET("stores/{storeId}/promotions")
    suspend fun getStorePromotions(
        @Path("storeId")
        storeId: Long,
    ): List<PromotionResponse>

    @GET("stores/{storeId}/crowd-status")
    suspend fun getCrowdStatus(
        @Path("storeId")
        storeId: Long,
    ): CrowdStatusResponse
}
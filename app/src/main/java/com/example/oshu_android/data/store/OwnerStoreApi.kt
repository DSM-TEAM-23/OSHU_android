package com.example.oshu_android.data.store

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

/** JWT가 필요한 점주 가게·타임세일·홍보 API. */
interface OwnerStoreApi {

    @GET("owner/stores")
    suspend fun getMyStores(): Response<List<StoreCardResponse>>

    @POST("owner/stores")
    suspend fun createStore(
        @Body request: StoreCreateRequest,
    ): Response<StoreDetailResponse>

    @GET("owner/stores/{storeId}")
    suspend fun getMyStore(
        @Path("storeId") storeId: Long,
    ): Response<StoreDetailResponse>

    @PATCH("owner/stores/{storeId}")
    suspend fun updateStore(
        @Path("storeId") storeId: Long,
        @Body request: StoreUpdateRequest,
    ): Response<StoreDetailResponse>

    @PATCH("owner/stores/{storeId}/crowd-status")
    suspend fun updateCrowdStatus(
        @Path("storeId") storeId: Long,
        @Body request: CrowdStatusRequest,
    ): Response<CrowdStatusResponse>

    @POST("owner/stores/{storeId}/time-sales")
    suspend fun createTimeSale(
        @Path("storeId") storeId: Long,
        @Body request: TimeSaleRequest,
    ): Response<TimeSaleResponse>

    @PATCH("owner/time-sales/{timeSaleId}")
    suspend fun updateTimeSale(
        @Path("timeSaleId") timeSaleId: Long,
        @Body request: TimeSaleRequest,
    ): Response<TimeSaleResponse>

    @PATCH("owner/time-sales/{timeSaleId}/close")
    suspend fun closeTimeSale(
        @Path("timeSaleId") timeSaleId: Long,
    ): Response<TimeSaleResponse>

    @POST("owner/stores/{storeId}/promotions")
    suspend fun createPromotion(
        @Path("storeId") storeId: Long,
        @Body request: PromotionRequest,
    ): Response<PromotionResponse>

    @PATCH("owner/promotions/{promotionId}")
    suspend fun updatePromotion(
        @Path("promotionId") promotionId: Long,
        @Body request: PromotionRequest,
    ): Response<PromotionResponse>

    @DELETE("owner/promotions/{promotionId}")
    suspend fun deletePromotion(
        @Path("promotionId") promotionId: Long,
    ): Response<Unit>
}

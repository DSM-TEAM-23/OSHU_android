package com.example.oshu_android.data.store

import com.example.oshu_android.data.network.ApiResult
import com.example.oshu_android.data.network.executeApiCall
import com.example.oshu_android.data.network.executeUnitApiCall

/** 점주 화면에서 사용하는 가게, 혼잡도, 타임세일, 홍보 API 진입점입니다. */
class OwnerStoreRepository(
    private val ownerStoreApi: OwnerStoreApi,
) {

    suspend fun getMyStores(): ApiResult<List<StoreCardResponse>> =
        executeApiCall("내 가게 목록을 불러오지 못했습니다.") {
            ownerStoreApi.getMyStores()
        }

    suspend fun createStore(
        request: StoreCreateRequest,
    ): ApiResult<StoreDetailResponse> =
        executeApiCall("가게를 등록하지 못했습니다.") {
            ownerStoreApi.createStore(request)
        }

    suspend fun getMyStore(
        storeId: Long,
    ): ApiResult<StoreDetailResponse> =
        executeApiCall("내 가게 정보를 불러오지 못했습니다.") {
            ownerStoreApi.getMyStore(storeId)
        }

    suspend fun updateStore(
        storeId: Long,
        request: StoreUpdateRequest,
    ): ApiResult<StoreDetailResponse> =
        executeApiCall("가게 정보를 수정하지 못했습니다.") {
            ownerStoreApi.updateStore(storeId, request)
        }

    suspend fun updateCrowdStatus(
        storeId: Long,
        request: CrowdStatusRequest,
    ): ApiResult<CrowdStatusResponse> =
        executeApiCall("혼잡도를 갱신하지 못했습니다.") {
            ownerStoreApi.updateCrowdStatus(storeId, request)
        }

    suspend fun createTimeSale(
        storeId: Long,
        request: TimeSaleRequest,
    ): ApiResult<TimeSaleResponse> =
        executeApiCall("타임세일을 등록하지 못했습니다.") {
            ownerStoreApi.createTimeSale(storeId, request)
        }

    suspend fun updateTimeSale(
        timeSaleId: Long,
        request: TimeSaleRequest,
    ): ApiResult<TimeSaleResponse> =
        executeApiCall("타임세일을 수정하지 못했습니다.") {
            ownerStoreApi.updateTimeSale(timeSaleId, request)
        }

    suspend fun closeTimeSale(
        timeSaleId: Long,
    ): ApiResult<TimeSaleResponse> =
        executeApiCall("타임세일을 종료하지 못했습니다.") {
            ownerStoreApi.closeTimeSale(timeSaleId)
        }

    suspend fun createPromotion(
        storeId: Long,
        request: PromotionRequest,
    ): ApiResult<PromotionResponse> =
        executeApiCall("홍보를 등록하지 못했습니다.") {
            ownerStoreApi.createPromotion(storeId, request)
        }

    suspend fun updatePromotion(
        promotionId: Long,
        request: PromotionRequest,
    ): ApiResult<PromotionResponse> =
        executeApiCall("홍보를 수정하지 못했습니다.") {
            ownerStoreApi.updatePromotion(promotionId, request)
        }

    suspend fun deletePromotion(
        promotionId: Long,
    ): ApiResult<Unit> =
        executeUnitApiCall("홍보를 삭제하지 못했습니다.") {
            ownerStoreApi.deletePromotion(promotionId)
        }
}

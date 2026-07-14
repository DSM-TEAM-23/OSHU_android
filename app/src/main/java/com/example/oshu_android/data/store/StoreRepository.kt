package com.example.oshu_android.data.store

import com.example.oshu_android.data.network.ApiResult
import com.example.oshu_android.data.network.executeApiCall
import kotlinx.coroutines.CancellationException
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface MapStoreResult {

    data class Success(
        val stores: List<StoreCardResponse>,
    ) : MapStoreResult

    data class Failure(
        val message: String,
    ) : MapStoreResult
}

sealed interface StoreListResult {

    data class Success(
        val stores: List<StoreCardResponse>,
    ) : StoreListResult

    data class Failure(
        val message: String,
    ) : StoreListResult
}

sealed interface PromotionListResult {

    data class Success(
        val promotions: List<PromotionResponse>,
    ) : PromotionListResult

    data class Failure(
        val message: String,
    ) : PromotionListResult
}

class StoreRepository(
    private val storeApi: StoreApi,
) {

    suspend fun getStoreDetail(
        storeId: Long,
    ): ApiResult<StoreDetailResponse> =
        executeApiCall("가게 상세 정보를 불러오지 못했습니다.") {
            storeApi.getStoreDetail(storeId)
        }

    suspend fun getStoreSummary(
        storeId: Long,
    ): ApiResult<StoreCardResponse> =
        executeApiCall("가게 요약 정보를 불러오지 못했습니다.") {
            storeApi.getStoreSummary(storeId)
        }

    suspend fun getStorePromotions(
        storeId: Long,
    ): ApiResult<List<PromotionResponse>> =
        executeApiCall("가게 행사를 불러오지 못했습니다.") {
            storeApi.getStorePromotions(storeId)
        }

    suspend fun getCrowdStatus(
        storeId: Long,
    ): ApiResult<CrowdStatusResponse> =
        executeApiCall("가게 혼잡도를 불러오지 못했습니다.") {
            storeApi.getCrowdStatus(storeId)
        }

    suspend fun getPromotionDetail(
        promotionId: Long,
    ): ApiResult<PromotionResponse> =
        executeApiCall("행사 상세 정보를 불러오지 못했습니다.") {
            storeApi.getPromotionDetail(promotionId)
        }

    suspend fun getStores(
        keyword: String? = null,
        category: String? = null,
        page: Int = 0,
        size: Int = 20,
    ): StoreListResult {
        return try {
            val response = storeApi.getStores(
                keyword = keyword,
                category = category,
                page = page,
                size = size,
            )

            when {
                response.isSuccessful -> {
                    StoreListResult.Success(
                        stores = response.body()?.content.orEmpty(),
                    )
                }

                response.code() == 401 -> {
                    StoreListResult.Failure(
                        message = "로그인 후 매장 정보를 확인해주세요.",
                    )
                }

                response.code() in 500..599 -> {
                    StoreListResult.Failure(
                        message = "매장 정보를 불러오지 못했습니다.",
                    )
                }

                else -> {
                    StoreListResult.Failure(
                        message = "매장 목록 조회에 실패했습니다.",
                    )
                }
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: SocketTimeoutException) {
            StoreListResult.Failure(
                message = "서버 응답 시간이 초과되었습니다.",
            )
        } catch (exception: IOException) {
            StoreListResult.Failure(
                message = "네트워크 연결을 확인해주세요.",
            )
        } catch (exception: Exception) {
            StoreListResult.Failure(
                message = "매장 정보를 불러오는 중 오류가 발생했습니다.",
            )
        }
    }

    suspend fun getMapStores(
        latitude: Double,
        longitude: Double,
        radius: Int,
        timeSaleOnly: Boolean = false,
    ): MapStoreResult {
        return try {
            val response = storeApi.getMapStores(
                latitude = latitude,
                longitude = longitude,
                radius = radius,
                timeSaleOnly = timeSaleOnly,
            )

            when {
                response.isSuccessful -> {
                    MapStoreResult.Success(
                        stores = response.body().orEmpty(),
                    )
                }

                response.code() == 400 -> {
                    MapStoreResult.Failure(
                        message = "지도 조회 위치 정보를 확인해주세요.",
                    )
                }

                response.code() == 401 -> {
                    MapStoreResult.Failure(
                        message = "로그인 후 지도 정보를 확인해주세요.",
                    )
                }

                response.code() in 500..599 -> {
                    MapStoreResult.Failure(
                        message = "가게 정보를 불러오지 못했습니다.",
                    )
                }

                else -> {
                    MapStoreResult.Failure(
                        message = "지도 조회에 실패했습니다.",
                    )
                }
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: SocketTimeoutException) {
            MapStoreResult.Failure(
                message = "서버 응답 시간이 초과되었습니다.",
            )
        } catch (exception: IOException) {
            MapStoreResult.Failure(
                message = "네트워크 연결을 확인해주세요.",
            )
        } catch (exception: Exception) {
            MapStoreResult.Failure(
                message = "가게 정보를 불러오는 중 오류가 발생했습니다.",
            )
        }
    }

    suspend fun getPromotions(
        status: String? = null,
        page: Int = 0,
        size: Int = 20,
    ): PromotionListResult {
        return try {
            val response = storeApi.getPromotions(
                status = status,
                page = page,
                size = size,
            )

            when {
                response.isSuccessful -> {
                    PromotionListResult.Success(
                        promotions = response.body()?.content.orEmpty(),
                    )
                }

                response.code() == 401 -> {
                    PromotionListResult.Failure(
                        message = "로그인 후 프로모션을 확인해주세요.",
                    )
                }

                response.code() in 500..599 -> {
                    PromotionListResult.Failure(
                        message = "프로모션 정보를 불러오지 못했습니다.",
                    )
                }

                else -> {
                    PromotionListResult.Failure(
                        message = "프로모션 조회에 실패했습니다.",
                    )
                }
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: SocketTimeoutException) {
            PromotionListResult.Failure(
                message = "서버 응답 시간이 초과되었습니다.",
            )
        } catch (exception: IOException) {
            PromotionListResult.Failure(
                message = "네트워크 연결을 확인해주세요.",
            )
        } catch (exception: Exception) {
            PromotionListResult.Failure(
                message = "프로모션을 불러오는 중 오류가 발생했습니다.",
            )
        }
    }
}

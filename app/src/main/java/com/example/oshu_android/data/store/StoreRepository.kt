package com.example.oshu_android.data.store

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

    suspend fun getMapStores(
        latitude: Double,
        longitude: Double,
        radius: Int,
    ): MapStoreResult {
        return try {
            val response = storeApi.getMapStores(
                latitude = latitude,
                longitude = longitude,
                radius = radius,
                timeSaleOnly = false,
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

    suspend fun getPromotions(): PromotionListResult {
        return try {
            val response = storeApi.getPromotions()

            when {
                response.isSuccessful -> {
                    PromotionListResult.Success(
                        promotions = response.body()?.content.orEmpty(),
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
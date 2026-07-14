package com.example.oshu_android.data.store

import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

interface StoreRepository {

    suspend fun getMapStores(
        latitude: Double,
        longitude: Double,
        radius: Int,
        timeSaleOnly: Boolean,
    ): Result<List<StoreCardResponse>>
}

class StoreRepositoryImpl(
    private val storeApi: StoreApi,
) : StoreRepository {

    override suspend fun getMapStores(
        latitude: Double,
        longitude: Double,
        radius: Int,
        timeSaleOnly: Boolean,
    ): Result<List<StoreCardResponse>> {
        return try {
            val stores = storeApi.getMapStores(
                latitude = latitude,
                longitude = longitude,
                radius = radius,
                timeSaleOnly = timeSaleOnly,
            )

            Result.success(stores)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: IOException) {
            Result.failure(
                StoreRepositoryException(
                    "네트워크 연결을 확인해주세요."
                )
            )
        } catch (exception: HttpException) {
            Result.failure(
                StoreRepositoryException(
                    httpErrorMessage(
                        code = exception.code()
                    )
                )
            )
        } catch (_: Exception) {
            Result.failure(
                StoreRepositoryException(
                    "가게 정보를 불러오지 못했습니다."
                )
            )
        }
    }

    private fun httpErrorMessage(
        code: Int,
    ): String {
        return when (code) {
            400 -> "지도 조회 요청이 올바르지 않습니다."
            401 -> "로그인이 필요합니다."
            403 -> "지도 조회 권한이 없습니다."
            404 -> "가게 정보를 찾을 수 없습니다."
            in 500..599 ->
                "서버에 문제가 발생했습니다."

            else ->
                "가게 정보를 불러오지 못했습니다."
        }
    }
}

class StoreRepositoryException(
    override val message: String,
) : Exception(message)
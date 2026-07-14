package com.example.oshu_android.data.network

import kotlinx.coroutines.CancellationException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

/** 화면 계층이 HTTP/네트워크 예외를 직접 처리하지 않도록 하는 공통 결과 타입입니다. */
sealed interface ApiResult<out T> {

    data class Success<T>(
        val data: T,
    ) : ApiResult<T>

    data class Failure(
        val message: String,
        val statusCode: Int? = null,
    ) : ApiResult<Nothing>
}

suspend fun <T> executeApiCall(
    defaultErrorMessage: String,
    request: suspend () -> Response<T>,
): ApiResult<T> {
    return try {
        val response = request()

        if (!response.isSuccessful) {
            return ApiResult.Failure(
                message = response.errorMessageOr(defaultErrorMessage),
                statusCode = response.code(),
            )
        }

        val body = response.body()
            ?: return ApiResult.Failure(
                message = "서버 응답이 비어 있습니다.",
                statusCode = response.code(),
            )

        ApiResult.Success(body)
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: SocketTimeoutException) {
        ApiResult.Failure("서버 응답 시간이 초과되었습니다.")
    } catch (exception: IOException) {
        ApiResult.Failure("네트워크 연결을 확인해주세요.")
    } catch (_: Exception) {
        ApiResult.Failure(defaultErrorMessage)
    }
}

suspend fun executeUnitApiCall(
    defaultErrorMessage: String,
    request: suspend () -> Response<Unit>,
): ApiResult<Unit> {
    return try {
        val response = request()

        if (response.isSuccessful) {
            ApiResult.Success(Unit)
        } else {
            ApiResult.Failure(
                message = response.errorMessageOr(defaultErrorMessage),
                statusCode = response.code(),
            )
        }
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: SocketTimeoutException) {
        ApiResult.Failure("서버 응답 시간이 초과되었습니다.")
    } catch (exception: IOException) {
        ApiResult.Failure("네트워크 연결을 확인해주세요.")
    } catch (_: Exception) {
        ApiResult.Failure(defaultErrorMessage)
    }
}

private fun Response<*>.errorMessageOr(
    defaultMessage: String,
): String {
    val errorBody = runCatching {
        errorBody()?.string()
    }.getOrNull().orEmpty()

    val serverMessage = """"message"\s*:\s*"([^"]+)"""".toRegex()
        .find(errorBody)
        ?.groupValues
        ?.getOrNull(1)

    return serverMessage ?: defaultMessage
}

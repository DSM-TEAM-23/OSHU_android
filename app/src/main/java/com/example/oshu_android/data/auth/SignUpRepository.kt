package com.example.oshu_android.data.auth

import android.util.Log
import kotlinx.coroutines.CancellationException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed interface SignUpResult {

    data class Success(
        val user: SignUpResponse,
    ) : SignUpResult

    data object DuplicateLoginId : SignUpResult

    data class Error(
        val message: String,
    ) : SignUpResult
}

class SignUpRepository(
    private val authApi: AuthApi,
) {

    suspend fun signUp(
        loginId: String,
        password: String,
    ): SignUpResult {
        return try {
            Log.d(
                TAG,
                "회원가입 요청 시작: loginId=$loginId",
            )

            val response = authApi.signUp(
                SignUpRequest(
                    loginId = loginId,
                    password = password,
                ),
            )

            val responseBody = response.body()
            val errorBody = response.errorBody()?.string()

            Log.d(
                TAG,
                "회원가입 응답: code=${response.code()}, message=${response.message()}, body=$responseBody, error=$errorBody",
            )

            when {
                response.isSuccessful -> {
                    if (responseBody != null) {
                        Log.d(
                            TAG,
                            "회원가입 성공: ${responseBody.message}",
                        )

                        SignUpResult.Success(
                            user = responseBody,
                        )
                    } else {
                        Log.e(
                            TAG,
                            "회원가입 성공 응답의 body가 비어 있습니다.",
                        )

                        SignUpResult.Error(
                            message = "회원가입 응답이 비어 있습니다.",
                        )
                    }
                }

                response.code() == 409 -> {
                    Log.w(
                        TAG,
                        "이미 사용 중인 아이디입니다.",
                    )

                    SignUpResult.DuplicateLoginId
                }

                response.code() == 400 -> {
                    SignUpResult.Error(
                        message = extractErrorMessage(
                            errorBody = errorBody,
                            defaultMessage = "회원가입 정보를 다시 확인해주세요.",
                        ),
                    )
                }

                response.code() in 500..599 -> {
                    SignUpResult.Error(
                        message = extractErrorMessage(
                            errorBody = errorBody,
                            defaultMessage = "서버에 문제가 발생했습니다.",
                        ),
                    )
                }

                else -> {
                    SignUpResult.Error(
                        message = extractErrorMessage(
                            errorBody = errorBody,
                            defaultMessage = "회원가입에 실패했습니다. 오류 코드: ${response.code()}",
                        ),
                    )
                }
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: UnknownHostException) {
            Log.e(
                TAG,
                "서버 주소를 찾을 수 없습니다.",
                exception,
            )

            SignUpResult.Error(
                message = "서버 주소를 찾을 수 없습니다.",
            )
        } catch (exception: ConnectException) {
            Log.e(
                TAG,
                "서버 연결에 실패했습니다.",
                exception,
            )

            SignUpResult.Error(
                message = "서버에 연결할 수 없습니다.",
            )
        } catch (exception: SocketTimeoutException) {
            Log.e(
                TAG,
                "서버 응답 시간이 초과되었습니다.",
                exception,
            )

            SignUpResult.Error(
                message = "서버 응답 시간이 초과되었습니다.",
            )
        } catch (exception: IOException) {
            Log.e(
                TAG,
                "네트워크 통신에 실패했습니다.",
                exception,
            )

            SignUpResult.Error(
                message = exception.message
                    ?: "네트워크 연결을 확인해주세요.",
            )
        } catch (exception: Exception) {
            Log.e(
                TAG,
                "회원가입 처리 중 오류가 발생했습니다.",
                exception,
            )

            SignUpResult.Error(
                message = exception.message
                    ?: "회원가입 처리 중 오류가 발생했습니다.",
            )
        }
    }

    private fun extractErrorMessage(
        errorBody: String?,
        defaultMessage: String,
    ): String {
        if (errorBody.isNullOrBlank()) {
            return defaultMessage
        }

        val messagePattern =
            """"message"\s*:\s*"([^"]+)"""".toRegex()

        return messagePattern
            .find(errorBody)
            ?.groupValues
            ?.getOrNull(1)
            ?: defaultMessage
    }

    private companion object {
        const val TAG = "SignUpRepository"
    }
}
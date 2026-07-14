package com.example.oshu_android.data.auth

import kotlinx.coroutines.CancellationException
import java.io.IOException

sealed interface SignUpResult {

    data class Success(
        val message: String,
    ) : SignUpResult

    data object DuplicateLoginId :
        SignUpResult

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
            val response =
                authApi.signUp(
                    request =
                        SignUpRequest(
                            loginId = loginId,
                            password = password,
                        ),
                )

            when {
                response.isSuccessful -> {
                    val message =
                        response.body()
                            ?.message
                            ?.takeIf {
                                it.isNotBlank()
                            }
                            ?: "회원가입이 완료되었습니다."

                    SignUpResult.Success(
                        message = message,
                    )
                }

                response.code() == 400 -> {
                    SignUpResult.Error(
                        message =
                            "아이디와 비밀번호를 확인해주세요.",
                    )
                }

                response.code() == 409 -> {
                    SignUpResult.DuplicateLoginId
                }

                response.code() in 500..599 -> {
                    SignUpResult.Error(
                        message =
                            "서버에 문제가 발생했습니다.",
                    )
                }

                else -> {
                    SignUpResult.Error(
                        message =
                            "회원가입에 실패했습니다.",
                    )
                }
            }
        } catch (
            cancellation:
            CancellationException,
        ) {
            throw cancellation
        } catch (_: IOException) {
            SignUpResult.Error(
                message =
                    "네트워크 연결을 확인해주세요.",
            )
        } catch (_: Exception) {
            SignUpResult.Error(
                message =
                    "회원가입 중 오류가 발생했습니다.",
            )
        }
    }
}
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
            val response = authApi.signUp(
                SignUpRequest(
                    loginId = loginId,
                    password = password,
                ),
            )

            Log.d(
                TAG,
                "signUp response code=${response.code()} message=${response.message()} error=${response.errorBody()?.string()}",
            )

            when {
                response.isSuccessful -> {
                    val body = response.body()

                    if (body != null) {
                        SignUpResult.Success(body)
                    } else {
                        SignUpResult.Error(
                            "회원가입 응답이 비어 있습니다.",
                        )
                    }
                }

                response.code() == 409 -> {
                    SignUpResult.DuplicateLoginId
                }

                response.code() == 400 -> {
                    SignUpResult.Error(
                        "회원가입 정보를 다시 확인해주세요.",
                    )
                }

                response.code() in 500..599 -> {
                    SignUpResult.Error(
                        "서버에 문제가 발생했습니다.",
                    )
                }

                else -> {
                    SignUpResult.Error(
                        "회원가입에 실패했습니다. 오류 코드: ${response.code()}",
                    )
                }
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: UnknownHostException) {
            Log.e(TAG, "서버 주소를 찾을 수 없습니다.", exception)

            SignUpResult.Error(
                "서버 주소를 찾을 수 없습니다.",
            )
        } catch (exception: ConnectException) {
            Log.e(TAG, "서버 연결에 실패했습니다.", exception)

            SignUpResult.Error(
                "서버가 실행 중인지 확인해주세요.",
            )
        } catch (exception: SocketTimeoutException) {
            Log.e(TAG, "서버 응답 시간이 초과되었습니다.", exception)

            SignUpResult.Error(
                "서버 응답 시간이 초과되었습니다.",
            )
        } catch (exception: IOException) {
            Log.e(TAG, "네트워크 통신에 실패했습니다.", exception)

            SignUpResult.Error(
                "네트워크 연결을 확인해주세요.",
            )
        } catch (exception: Exception) {
            Log.e(TAG, "회원가입 처리 중 오류가 발생했습니다.", exception)

            SignUpResult.Error(
                "회원가입 처리 중 오류가 발생했습니다.",
            )
        }
    }

    private companion object {
        const val TAG = "SignUpRepository"
    }
}
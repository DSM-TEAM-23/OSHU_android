package com.example.oshu_android.data.auth

import android.util.Log
import com.example.oshu_android.feature.auth.login.LoginResult
import kotlinx.coroutines.CancellationException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

interface LoginRepository {

    suspend fun login(
        loginId: String,
        password: String,
        keepLoggedIn: Boolean,
    ): LoginResult
}

interface SessionStore {

    suspend fun save(
        response: LoginResponse,
        persist: Boolean,
    )
}

class LoginRepositoryImpl(
    private val authApi: AuthApi,
    private val sessionStore: SessionStore,
) : LoginRepository {

    override suspend fun login(
        loginId: String,
        password: String,
        keepLoggedIn: Boolean,
    ): LoginResult {
        return try {
            Log.d(
                TAG,
                "로그인 요청 시작: loginId=$loginId",
            )

            val response = authApi.login(
                LoginRequest(
                    loginId = loginId,
                    password = password,
                ),
            )

            val responseBody = response.body()
            val errorBody = response.errorBody()?.string()

            Log.d(
                TAG,
                "로그인 응답: code=${response.code()}, message=${response.message()}, body=$responseBody, error=$errorBody",
            )

            when {
                response.isSuccessful -> {
                    val body = responseBody
                        ?: return LoginResult.Failure(
                            message = "서버 응답이 올바르지 않습니다.",
                        )

                    sessionStore.save(
                        response = body,
                        persist = keepLoggedIn,
                    )

                    Log.d(
                        TAG,
                        "로그인 성공: userId=${body.userId}",
                    )

                    LoginResult.Success(
                        user = body.toLoggedInUser(),
                    )
                }

                response.code() == 400 ||
                        response.code() == 401 -> {
                    Log.w(
                        TAG,
                        "아이디 또는 비밀번호가 올바르지 않습니다.",
                    )

                    LoginResult.InvalidCredentials
                }

                response.code() in 500..599 -> {
                    LoginResult.Failure(
                        message = extractErrorMessage(
                            errorBody = errorBody,
                            defaultMessage = "서버에 문제가 발생했습니다.",
                        ),
                    )
                }

                else -> {
                    LoginResult.Failure(
                        message = extractErrorMessage(
                            errorBody = errorBody,
                            defaultMessage = "로그인에 실패했습니다. 오류 코드: ${response.code()}",
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

            LoginResult.Failure(
                message = "서버 주소를 찾을 수 없습니다.",
            )
        } catch (exception: ConnectException) {
            Log.e(
                TAG,
                "서버 연결에 실패했습니다.",
                exception,
            )

            LoginResult.Failure(
                message = "서버에 연결할 수 없습니다.",
            )
        } catch (exception: SocketTimeoutException) {
            Log.e(
                TAG,
                "서버 응답 시간이 초과되었습니다.",
                exception,
            )

            LoginResult.Failure(
                message = "서버 응답 시간이 초과되었습니다.",
            )
        } catch (exception: IOException) {
            Log.e(
                TAG,
                "네트워크 통신에 실패했습니다.",
                exception,
            )

            LoginResult.Failure(
                message = exception.message
                    ?: "네트워크 연결을 확인해주세요.",
            )
        } catch (exception: Exception) {
            Log.e(
                TAG,
                "로그인 처리 중 오류가 발생했습니다.",
                exception,
            )

            LoginResult.Failure(
                message = exception.message
                    ?: "로그인 중 오류가 발생했습니다.",
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
        const val TAG = "LoginRepository"
    }
}
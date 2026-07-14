package com.example.oshu_android.data.auth

import com.example.oshu_android.feature.auth.login.LoginResult
import kotlinx.coroutines.CancellationException
import java.io.IOException
import java.net.SocketTimeoutException

interface LoginRepository {

    suspend fun login(
        loginId: String,
        password: String,
        keepLoggedIn: Boolean,
    ): LoginResult

    suspend fun loginWithGoogleTicket(
        code: String,
        keepLoggedIn: Boolean,
    ): LoginResult
}

interface SessionStore {

    fun getAccessToken(): String?

    suspend fun saveAccessToken(
        accessToken: String,
        persist: Boolean,
    )

    suspend fun clear()
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
            val response = authApi.login(
                LoginRequest(
                    loginId = loginId,
                    password = password,
                ),
            )

            when {
                response.isSuccessful -> {
                    val body = response.body()
                        ?: return LoginResult.Failure(
                            message = "서버 응답이 올바르지 않습니다.",
                        )

                    if (body.accessToken.isBlank()) {
                        return LoginResult.Failure(
                            message = "로그인 토큰을 확인할 수 없습니다.",
                        )
                    }

                    sessionStore.saveAccessToken(
                        accessToken = body.accessToken,
                        persist = keepLoggedIn,
                    )

                    LoginResult.Success
                }

                response.code() == 400 ||
                        response.code() == 401 -> {
                    LoginResult.InvalidCredentials
                }

                response.code() in 500..599 -> {
                    LoginResult.Failure(
                        message = "서버에 문제가 발생했습니다.",
                    )
                }

                else -> {
                    LoginResult.Failure(
                        message = "로그인에 실패했습니다.",
                    )
                }
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: SocketTimeoutException) {
            LoginResult.Failure(
                message = "서버 응답 시간이 초과되었습니다.",
            )
        } catch (exception: IOException) {
            LoginResult.Failure(
                message = "네트워크 연결을 확인해주세요.",
            )
        } catch (exception: Exception) {
            LoginResult.Failure(
                message = "로그인 중 오류가 발생했습니다.",
            )
        }
    }

    override suspend fun loginWithGoogleTicket(
        code: String,
        keepLoggedIn: Boolean,
    ): LoginResult {
        if (code.isBlank()) {
            return LoginResult.Failure(
                message = "구글 로그인 정보를 확인할 수 없습니다.",
            )
        }

        return try {
            val response = authApi.exchangeGoogleTicket(
                GoogleTokenExchangeRequest(
                    code = code,
                ),
            )

            when {
                response.isSuccessful -> {
                    val body = response.body()
                        ?: return LoginResult.Failure(
                            message = "서버 응답이 올바르지 않습니다.",
                        )

                    if (
                        body.accessToken.isBlank() ||
                        !body.tokenType.equals("Bearer", ignoreCase = true)
                    ) {
                        return LoginResult.Failure(
                            message = "로그인 토큰을 확인할 수 없습니다.",
                        )
                    }

                    sessionStore.saveAccessToken(
                        accessToken = body.accessToken,
                        persist = keepLoggedIn,
                    )

                    LoginResult.Success
                }

                response.code() in 500..599 -> {
                    LoginResult.Failure(
                        message = "서버에 문제가 발생했습니다.",
                    )
                }

                else -> {
                    LoginResult.Failure(
                        message = "구글 로그인에 실패했습니다.",
                    )
                }
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: SocketTimeoutException) {
            LoginResult.Failure(
                message = "서버 응답 시간이 초과되었습니다.",
            )
        } catch (exception: IOException) {
            LoginResult.Failure(
                message = "네트워크 연결을 확인해주세요.",
            )
        } catch (exception: Exception) {
            LoginResult.Failure(
                message = "로그인 중 오류가 발생했습니다.",
            )
        }
    }
}

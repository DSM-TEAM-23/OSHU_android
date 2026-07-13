package com.example.oshu_android.data.auth

import com.example.oshu_android.feature.auth.login.LoginResult
import kotlinx.coroutines.CancellationException
import java.io.IOException

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
                            "서버 응답이 올바르지 않습니다.",
                        )

                    sessionStore.save(
                        response = body,
                        persist = keepLoggedIn,
                    )

                    LoginResult.Success(
                        user = body.toLoggedInUser(),
                    )
                }

                response.code() == 400 -> {
                    LoginResult.InvalidCredentials
                }

                response.code() in 500..599 -> {
                    LoginResult.Failure(
                        "서버에 문제가 발생했습니다.",
                    )
                }

                else -> {
                    LoginResult.Failure(
                        "로그인에 실패했습니다.",
                    )
                }
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: IOException) {
            LoginResult.Failure(
                "네트워크 연결을 확인해주세요.",
            )
        } catch (_: Exception) {
            LoginResult.Failure(
                "로그인 중 오류가 발생했습니다.",
            )
        }
    }
}
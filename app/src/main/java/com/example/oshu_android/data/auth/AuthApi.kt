package com.example.oshu_android.data.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): Response<LoginResponse>
}

object OshuApiConfig {
    const val BASE_URL = "https://api.oshu.kr/api/v1/"
}
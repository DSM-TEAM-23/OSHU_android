package com.example.oshu_android.data.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): Response<LoginResponse>

    @POST("auth/google/exchange")
    suspend fun exchangeGoogleTicket(
        @Body request: GoogleTokenExchangeRequest,
    ): Response<GoogleTokenExchangeResponse>

    @POST("auth/signup")
    suspend fun signUp(
        @Body request: SignUpRequest,
    ): Response<SignUpResponse>
}

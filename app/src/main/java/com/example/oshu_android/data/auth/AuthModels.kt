package com.example.oshu_android.data.auth

data class LoginRequest(
    val loginId: String,
    val password: String,
)

data class LoginResponse(
    val accessToken: String,
    val tokenType: String,
)

data class GoogleTokenExchangeRequest(
    val code: String,
)

data class GoogleTokenExchangeResponse(
    val accessToken: String,
    val tokenType: String,
)

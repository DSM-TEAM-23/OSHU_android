package com.example.oshu_android.data.auth

data class LoginRequest(
    val loginId: String,
    val password: String,
)

data class LoginResponse(
    val accessToken: String,
    val tokenType: String,
)
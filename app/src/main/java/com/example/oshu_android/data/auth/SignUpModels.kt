package com.example.oshu_android.data.auth

data class SignUpRequest(
    val loginId: String,
    val password: String,
)

data class SignUpResponse(
    val message: String = "",
)
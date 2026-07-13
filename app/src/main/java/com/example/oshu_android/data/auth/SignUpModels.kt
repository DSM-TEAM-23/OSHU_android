package com.example.oshu_android.data.auth

data class SignUpRequest(
    val loginId: String,
    val password: String,
)

data class SignUpResponse(
    val userId: Long,
    val loginId: String,
    val nickname: String?,
    val role: String?,
)
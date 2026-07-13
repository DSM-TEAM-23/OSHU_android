package com.example.oshu_android.data.auth

import com.example.oshu_android.feature.auth.login.LoggedInUser
import com.example.oshu_android.feature.auth.login.UserRole

data class LoginRequest(
    val loginId: String,
    val password: String,
)

data class LoginResponse(
    val userId: Long,
    val loginId: String,
    val nickname: String? = null,
    val role: String,
    val accessToken: String,
    val refreshToken: String,
) {
    fun toLoggedInUser(): LoggedInUser = LoggedInUser(
        userId = userId,
        loginId = loginId,
        nickname = nickname,
        role = runCatching { UserRole.valueOf(role) }
            .getOrDefault(UserRole.CONSUMER),
    )
}

data class ErrorResponse(
    val code: String? = null,
    val message: String? = null,
    val status: Int? = null,
    val timestamp: String? = null,
)

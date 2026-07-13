package com.example.oshu_android.feature.auth.login

data class LoginUiState(
    val loginId: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val keepLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val loginIdError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,
    val isLoginSuccessful: Boolean = false,
)

data class LoggedInUser(
    val userId: Long,
    val loginId: String,
    val nickname: String?,
    val role: UserRole,
)

enum class UserRole {
    CONSUMER,
    OWNER,
}

sealed interface LoginResult {
    data class Success(val user: LoggedInUser) : LoginResult
    data object InvalidCredentials : LoginResult
    data class Failure(val message: String) : LoginResult
}

fun interface LoginRepository {
    suspend fun login(
        loginId: String,
        password: String,
        keepLoggedIn: Boolean,
    ): LoginResult
}

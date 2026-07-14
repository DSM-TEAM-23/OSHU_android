package com.example.oshu_android.feature.auth.login

data class LoginUiState(
    val loginId: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val keepLoggedIn: Boolean = false,
    val loginIdError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
)

sealed interface LoginResult {

    data object Success : LoginResult

    data object InvalidCredentials : LoginResult

    data class Failure(
        val message: String,
    ) : LoginResult
}
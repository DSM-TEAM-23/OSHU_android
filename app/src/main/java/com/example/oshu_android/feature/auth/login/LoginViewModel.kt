package com.example.oshu_android.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onLoginIdChanged(value: String) {
        _uiState.update {
            it.copy(
                loginId = value,
                loginIdError = null,
                loginError = null,
            )
        }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                passwordError = null,
                loginError = null,
            )
        }
    }

    fun onPasswordVisibilityChanged() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onKeepLoggedInChanged(checked: Boolean) {
        _uiState.update { it.copy(keepLoggedIn = checked) }
    }

    fun login() {
        val current = _uiState.value
        if (current.isLoading) return

        val normalizedLoginId = current.loginId.trim()
        val loginIdError = if (normalizedLoginId.isBlank()) {
            "아이디를 입력해주세요."
        } else {
            null
        }
        val passwordError = if (current.password.isBlank()) {
            "비밀번호를 입력해주세요."
        } else {
            null
        }

        if (loginIdError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    loginIdError = loginIdError,
                    passwordError = passwordError,
                    loginError = null,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    loginIdError = null,
                    passwordError = null,
                    loginError = null,
                )
            }

            val result = loginRepository.login(
                loginId = normalizedLoginId,
                password = current.password,
                keepLoggedIn = current.keepLoggedIn,
            )

            when (result) {
                is LoginResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            password = "",
                            isLoginSuccessful = true,
                        )
                    }
                }

                LoginResult.InvalidCredentials -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginError = "아이디 또는 비밀번호를 확인해주세요.",
                        )
                    }
                }

                is LoginResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginError = result.message,
                        )
                    }
                }
            }
        }
    }

    /** 화면 이동 후 성공 상태를 초기화해 재이동을 막습니다. */
    fun onLoginSuccessHandled() {
        _uiState.update { it.copy(isLoginSuccessful = false) }
    }

    class Factory(
        private val loginRepository: LoginRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(LoginViewModel::class.java))
            return LoginViewModel(loginRepository) as T
        }
    }
}

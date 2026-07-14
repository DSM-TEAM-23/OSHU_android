package com.example.oshu_android.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.oshu_android.data.auth.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> =
        _uiState.asStateFlow()

    fun onLoginIdChanged(
        loginId: String,
    ) {
        _uiState.update {
            it.copy(
                loginId = loginId,
                loginIdError = null,
                loginError = null,
            )
        }
    }

    fun onPasswordChanged(
        password: String,
    ) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = null,
                loginError = null,
            )
        }
    }

    fun onPasswordVisibilityChanged() {
        _uiState.update {
            it.copy(
                isPasswordVisible =
                    !it.isPasswordVisible,
            )
        }
    }

    fun onKeepLoggedInChanged(
        keepLoggedIn: Boolean,
    ) {
        _uiState.update {
            it.copy(
                keepLoggedIn = keepLoggedIn,
            )
        }
    }

    fun login() {
        val currentState = _uiState.value

        if (currentState.isLoading) {
            return
        }

        val loginIdError =
            if (currentState.loginId.isBlank()) {
                "아이디를 입력해주세요."
            } else {
                null
            }

        val passwordError =
            if (currentState.password.isBlank()) {
                "비밀번호를 입력해주세요."
            } else {
                null
            }

        if (
            loginIdError != null ||
            passwordError != null
        ) {
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

            when (
                val result = loginRepository.login(
                    loginId = currentState.loginId.trim(),
                    password = currentState.password,
                    keepLoggedIn =
                        currentState.keepLoggedIn,
                )
            ) {
                LoginResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccessful = true,
                            loginError = null,
                        )
                    }
                }

                LoginResult.InvalidCredentials -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginError =
                                "아이디 또는 비밀번호가 일치하지 않습니다.",
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

    fun loginWithGoogleTicket(
        code: String,
    ) {
        if (code.isBlank() || _uiState.value.isLoading) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    loginError = null,
                )
            }

            when (
                val result = loginRepository.loginWithGoogleTicket(
                    code = code,
                    keepLoggedIn = _uiState.value.keepLoggedIn,
                )
            ) {
                LoginResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccessful = true,
                            loginError = null,
                        )
                    }
                }

                LoginResult.InvalidCredentials -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginError = "구글 로그인 정보를 확인할 수 없습니다.",
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

    fun onLoginSuccessHandled() {
        _uiState.update {
            it.copy(
                isLoginSuccessful = false,
            )
        }
    }

    class Factory(
        private val loginRepository:
        LoginRepository,
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(
            modelClass: Class<T>,
        ): T {
            if (
                modelClass.isAssignableFrom(
                    LoginViewModel::class.java,
                )
            ) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(
                    loginRepository = loginRepository,
                ) as T
            }

            throw IllegalArgumentException(
                "Unknown ViewModel class: ${modelClass.name}",
            )
        }
    }
}

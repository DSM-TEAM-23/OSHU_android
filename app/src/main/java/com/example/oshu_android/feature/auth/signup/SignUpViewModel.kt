package com.example.oshu_android.feature.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.oshu_android.data.auth.SignUpRepository
import com.example.oshu_android.data.auth.SignUpResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val signUpRepository: SignUpRepository,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(SignUpUiState())

    val uiState: StateFlow<SignUpUiState> =
        _uiState.asStateFlow()

    fun onLoginIdChanged(
        loginId: String,
    ) {
        _uiState.value = _uiState.value.copy(
            loginId = loginId,
            isLoginIdChecked = false,
            loginIdError = null,
            generalError = null,
        )
    }

    fun onPasswordChanged(
        password: String,
    ) {
        val currentState = _uiState.value

        _uiState.value = currentState.copy(
            password = password,
            passwordError = null,
            passwordConfirmError =
                if (
                    currentState.passwordConfirm.isNotEmpty() &&
                    password != currentState.passwordConfirm
                ) {
                    "비밀번호가 일치하지 않습니다."
                } else {
                    null
                },
            generalError = null,
        )
    }

    fun onPasswordConfirmChanged(
        passwordConfirm: String,
    ) {
        _uiState.value = _uiState.value.copy(
            passwordConfirm = passwordConfirm,
            passwordConfirmError =
                if (
                    passwordConfirm.isNotEmpty() &&
                    passwordConfirm !=
                    _uiState.value.password
                ) {
                    "비밀번호가 일치하지 않습니다."
                } else {
                    null
                },
            generalError = null,
        )
    }

    fun checkLoginId() {
        val loginId = _uiState.value.loginId.trim()

        if (!isValidLoginId(loginId)) {
            _uiState.value = _uiState.value.copy(
                loginId = loginId,
                isLoginIdChecked = false,
                loginIdError =
                    "영문과 숫자를 조합하여 6~20자로 입력해주세요.",
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            loginId = loginId,
            isLoginIdChecked = true,
            loginIdError = null,
        )
    }

    fun onAllTermsChanged(
        checked: Boolean,
    ) {
        _uiState.value = _uiState.value.copy(
            serviceTermsAgreed = checked,
            privacyTermsAgreed = checked,
            marketingTermsAgreed = checked,
            generalError = null,
        )
    }

    fun onServiceTermsChanged(
        checked: Boolean,
    ) {
        _uiState.value = _uiState.value.copy(
            serviceTermsAgreed = checked,
            generalError = null,
        )
    }

    fun onPrivacyTermsChanged(
        checked: Boolean,
    ) {
        _uiState.value = _uiState.value.copy(
            privacyTermsAgreed = checked,
            generalError = null,
        )
    }

    fun onMarketingTermsChanged(
        checked: Boolean,
    ) {
        _uiState.value = _uiState.value.copy(
            marketingTermsAgreed = checked,
            generalError = null,
        )
    }

    fun signUp() {
        val currentState = _uiState.value
        val loginId = currentState.loginId.trim()

        val loginIdError =
            when {
                !isValidLoginId(loginId) -> {
                    "영문과 숫자를 조합하여 6~20자로 입력해주세요."
                }

                !currentState.isLoginIdChecked -> {
                    "아이디 중복확인을 해주세요."
                }

                else -> null
            }

        val passwordError =
            if (!isValidPassword(currentState.password)) {
                "영문, 숫자, 특수문자를 포함하여 8자 이상 입력해주세요."
            } else {
                null
            }

        val passwordConfirmError =
            if (
                currentState.password !=
                currentState.passwordConfirm
            ) {
                "비밀번호가 일치하지 않습니다."
            } else {
                null
            }

        val termsError =
            if (!currentState.requiredTermsAgreed) {
                "필수 약관에 동의해주세요."
            } else {
                null
            }

        if (
            loginIdError != null ||
            passwordError != null ||
            passwordConfirmError != null ||
            termsError != null
        ) {
            _uiState.value = currentState.copy(
                loginId = loginId,
                loginIdError = loginIdError,
                passwordError = passwordError,
                passwordConfirmError =
                    passwordConfirmError,
                generalError = termsError,
            )
            return
        }

        _uiState.value = currentState.copy(
            isLoading = true,
            generalError = null,
        )

        viewModelScope.launch {
            when (
                val result = signUpRepository.signUp(
                    loginId = loginId,
                    password = currentState.password,
                )
            ) {
                is SignUpResult.Success -> {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                        )
                }

                SignUpResult.DuplicateLoginId -> {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            isLoginIdChecked = false,
                            loginIdError =
                                "이미 사용 중인 아이디입니다.",
                        )
                }

                is SignUpResult.Error -> {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            generalError =
                                result.message,
                        )
                }
            }
        }
    }

    fun onSuccessHandled() {
        _uiState.value = _uiState.value.copy(
            isSuccess = false,
        )
    }

    class Factory(
        private val signUpRepository:
        SignUpRepository,
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(
            modelClass: Class<T>,
        ): T {
            if (
                modelClass.isAssignableFrom(
                    SignUpViewModel::class.java
                )
            ) {
                @Suppress("UNCHECKED_CAST")
                return SignUpViewModel(
                    signUpRepository
                ) as T
            }

            throw IllegalArgumentException(
                "Unknown ViewModel class"
            )
        }
    }
}
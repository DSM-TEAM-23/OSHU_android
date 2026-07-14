package com.example.oshu_android.feature.auth.signup

private val loginIdPattern =
    Regex(
        "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$"
    )

private val passwordPattern =
    Regex(
        "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d\\s]).{8,}$"
    )

data class SignUpUiState(
    val loginId: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val isLoginIdChecked: Boolean = false,
    val serviceTermsAgreed: Boolean = false,
    val privacyTermsAgreed: Boolean = false,
    val marketingTermsAgreed: Boolean = false,
    val loginIdError: String? = null,
    val passwordError: String? = null,
    val passwordConfirmError: String? = null,
    val generalError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
) {
    val allTermsAgreed: Boolean
        get() {
            return serviceTermsAgreed &&
                    privacyTermsAgreed &&
                    marketingTermsAgreed
        }

    val requiredTermsAgreed: Boolean
        get() {
            return serviceTermsAgreed &&
                    privacyTermsAgreed
        }

    val canSubmit: Boolean
        get() {
            return isLoginIdChecked &&
                    isValidLoginId(loginId) &&
                    isValidPassword(password) &&
                    password == passwordConfirm &&
                    requiredTermsAgreed &&
                    !isLoading
        }
}

internal fun isValidLoginId(
    loginId: String,
): Boolean {
    return loginIdPattern.matches(
        loginId.trim()
    )
}

internal fun isValidPassword(
    password: String,
): Boolean {
    return passwordPattern.matches(
        password
    )
}
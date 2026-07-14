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

internal fun passwordValidationMessage(
    password: String,
    showEmptyError: Boolean,
): String? {
    if (password.isEmpty()) {
        return if (showEmptyError) {
            "비밀번호를 입력해주세요."
        } else {
            null
        }
    }

    if (password.length < 8) {
        return "비밀번호는 8자 이상 입력해주세요."
    }

    val hasEnglish =
        password.any {
            it in 'a'..'z' ||
                    it in 'A'..'Z'
        }

    if (!hasEnglish) {
        return "비밀번호에 영문을 포함해주세요."
    }

    val hasNumber =
        password.any {
            it.isDigit()
        }

    if (!hasNumber) {
        return "비밀번호에 숫자를 포함해주세요."
    }

    val hasSpecialCharacter =
        password.any {
            !it.isLetterOrDigit() &&
                    !it.isWhitespace()
        }

    if (!hasSpecialCharacter) {
        return "비밀번호에 특수문자를 포함해주세요."
    }

    return null
}
package com.example.oshu_android.data.auth

sealed interface SignUpResult {

    data class Success(
        val user: SignUpResponse,
    ) : SignUpResult

    data object DuplicateLoginId : SignUpResult

    data class Error(
        val message: String,
    ) : SignUpResult
}

class SignUpRepository(
    private val authApi: AuthApi,
) {

    suspend fun signUp(
        loginId: String,
        password: String,
    ): SignUpResult {
        return try {
            val response = authApi.signUp(
                SignUpRequest(
                    loginId = loginId,
                    password = password,
                )
            )

            when {
                response.isSuccessful -> {
                    val body = response.body()

                    if (body != null) {
                        SignUpResult.Success(body)
                    } else {
                        SignUpResult.Error(
                            "회원가입 응답을 확인할 수 없습니다."
                        )
                    }
                }

                response.code() == 409 -> {
                    SignUpResult.DuplicateLoginId
                }

                else -> {
                    SignUpResult.Error(
                        "회원가입에 실패했습니다."
                    )
                }
            }
        } catch (exception: Exception) {
            SignUpResult.Error(
                "네트워크 연결을 확인해주세요."
            )
        }
    }
}
package com.example.oshu_android.data.auth

import com.example.oshu_android.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthModule {
    private val retrofit: Retrofit by lazy {
        val baseUrl = BuildConfig.BASE_URL

        require(baseUrl.isNotBlank()) {
            "BASE_URL이 설정되지 않았습니다."
        }

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }
}
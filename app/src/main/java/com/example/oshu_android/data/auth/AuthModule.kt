package com.example.oshu_android.data.auth

import com.example.oshu_android.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthModule {
    private val retrofit: Retrofit by lazy {
        require(BuildConfig.BASE_URL.isNotBlank())

        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }
}
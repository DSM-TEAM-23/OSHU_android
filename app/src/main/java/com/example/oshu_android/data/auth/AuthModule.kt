package com.example.oshu_android.data.auth

import android.content.Context
import com.example.oshu_android.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthModule {
    @Volatile
    private var authApiInstance: AuthApi? = null

    fun provideAuthApi(context: Context): AuthApi {
        return authApiInstance ?: synchronized(this) {
            authApiInstance ?: createAuthApi(context).also {
                authApiInstance = it
            }
        }
    }

    private fun createAuthApi(context: Context): AuthApi {
        val configuredBaseUrl = context.getString(
            R.string.oshu_api_base_url
        ).trim()

        require(configuredBaseUrl.isNotBlank()) {
            "BASE_URL이 설정되지 않았습니다."
        }

        val baseUrl = if (configuredBaseUrl.endsWith("/")) {
            configuredBaseUrl
        } else {
            "$configuredBaseUrl/"
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(AuthApi::class.java)
    }
}

package com.example.oshu_android.data.store

import android.content.Context
import com.example.oshu_android.R
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object StoreModule {

    @Volatile
    private var repository: StoreRepository? = null

    fun provideStoreRepository(
        context: Context,
        accessTokenProvider: () -> String?,
    ): StoreRepository {
        return repository ?: synchronized(this) {
            repository ?: createRepository(
                context = context.applicationContext,
                accessTokenProvider = accessTokenProvider,
            ).also {
                repository = it
            }
        }
    }

    private fun createRepository(
        context: Context,
        accessTokenProvider: () -> String?,
    ): StoreRepository {
        val storeApi = createRetrofit(
            context = context,
            accessTokenProvider = accessTokenProvider,
        ).create(
            StoreApi::class.java,
        )

        return StoreRepository(
            storeApi = storeApi,
        )
    }

    private fun createRetrofit(
        context: Context,
        accessTokenProvider: () -> String?,
    ): Retrofit {
        val configuredBaseUrl = context.getString(
            R.string.oshu_api_base_url,
        ).trim()

        require(configuredBaseUrl.isNotBlank()) {
            "BASE_URL이 설정되지 않았습니다."
        }

        val baseUrl = if (configuredBaseUrl.endsWith("/")) {
            configuredBaseUrl
        } else {
            "$configuredBaseUrl/"
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = accessTokenProvider()
                    ?.trim()
                    .orEmpty()
                    .removePrefix("Bearer ")
                    .removePrefix("bearer ")

                val requestBuilder = chain.request()
                    .newBuilder()

                if (token.isNotBlank()) {
                    requestBuilder.header(
                        "Authorization",
                        "Bearer $token",
                    )
                }

                chain.proceed(
                    requestBuilder.build(),
                )
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create(),
            )
            .build()
    }
}
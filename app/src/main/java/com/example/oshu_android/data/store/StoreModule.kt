package com.example.oshu_android.data.store

import android.content.Context
import com.example.oshu_android.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object StoreModule {

    @Volatile
    private var repository:
            StoreRepository? = null

    fun provideStoreRepository(
        context: Context,
    ): StoreRepository {
        return repository
            ?: synchronized(this) {
                repository
                    ?: createRepository(
                        context =
                            context.applicationContext,
                    ).also {
                        repository = it
                    }
            }
    }

    private fun createRepository(
        context: Context,
    ): StoreRepository {
        val storeApi =
            createRetrofit(
                context = context,
            ).create(
                StoreApi::class.java,
            )

        return StoreRepository(
            storeApi = storeApi,
        )
    }

    private fun createRetrofit(
        context: Context,
    ): Retrofit {
        val configuredBaseUrl =
            context.getString(
                R.string.oshu_api_base_url,
            ).trim()

        require(
            configuredBaseUrl.isNotBlank(),
        ) {
            "BASE_URL이 설정되지 않았습니다."
        }

        val baseUrl =
            if (
                configuredBaseUrl.endsWith("/")
            ) {
                configuredBaseUrl
            } else {
                "$configuredBaseUrl/"
            }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                GsonConverterFactory.create(),
            )
            .build()
    }
}
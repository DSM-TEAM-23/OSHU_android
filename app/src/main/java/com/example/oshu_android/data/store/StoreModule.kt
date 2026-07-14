package com.example.oshu_android.data.store

import android.content.Context
import com.example.oshu_android.R
import com.example.oshu_android.data.inquiry.InquiryApi
import com.example.oshu_android.data.inquiry.InquiryRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object StoreModule {

    @Volatile
    private var repository: StoreRepository? = null

    @Volatile
    private var ownerStoreRepository: OwnerStoreRepository? = null

    @Volatile
    private var inquiryRepository: InquiryRepository? = null

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

    fun provideOwnerStoreRepository(
        context: Context,
        accessTokenProvider: () -> String?,
    ): OwnerStoreRepository {
        return ownerStoreRepository ?: synchronized(this) {
            ownerStoreRepository ?: OwnerStoreRepository(
                createRetrofit(
                    context = context.applicationContext,
                    accessTokenProvider = accessTokenProvider,
                ).create(OwnerStoreApi::class.java),
            ).also {
                ownerStoreRepository = it
            }
        }
    }

    fun provideInquiryRepository(
        context: Context,
        accessTokenProvider: () -> String?,
    ): InquiryRepository {
        return inquiryRepository ?: synchronized(this) {
            inquiryRepository ?: InquiryRepository(
                createRetrofit(
                    context = context.applicationContext,
                    accessTokenProvider = accessTokenProvider,
                ).create(InquiryApi::class.java),
            ).also {
                inquiryRepository = it
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

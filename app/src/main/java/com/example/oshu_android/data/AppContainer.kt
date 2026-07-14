package com.example.oshu_android.data

import android.content.Context
import com.example.oshu_android.data.auth.AuthModule
import com.example.oshu_android.data.auth.LoginRepository
import com.example.oshu_android.data.auth.LoginRepositoryImpl
import com.example.oshu_android.data.auth.SessionStore
import com.example.oshu_android.data.auth.SignUpRepository
import com.example.oshu_android.data.store.StoreModule
import com.example.oshu_android.feature.onboarding.OnboardingPreferences

class AppContainer(
    context: Context,
) {
    private val applicationContext = context.applicationContext

    private val authApi = AuthModule.provideAuthApi(
        applicationContext,
    )

    private val sessionStore: SessionStore = PreferencesSessionStore(
        context = applicationContext,
    )

    val onboardingPreferences = OnboardingPreferences(
        context = applicationContext,
    )

    val loginRepository: LoginRepository = LoginRepositoryImpl(
        authApi = authApi,
        sessionStore = sessionStore,
    )

    val signUpRepository = SignUpRepository(
        authApi = authApi,
    )

    val storeRepository = StoreModule.provideStoreRepository(
        context = applicationContext,
        accessTokenProvider = sessionStore::getAccessToken,
    )

    val ownerStoreRepository = StoreModule.provideOwnerStoreRepository(
        context = applicationContext,
        accessTokenProvider = sessionStore::getAccessToken,
    )

    val inquiryRepository = StoreModule.provideInquiryRepository(
        context = applicationContext,
        accessTokenProvider = sessionStore::getAccessToken,
    )
}

private class PreferencesSessionStore(
    context: Context,
) : SessionStore {

    private val preferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    private var sessionAccessToken: String? = preferences.getString(
        ACCESS_TOKEN_KEY,
        null,
    )

    override fun getAccessToken(): String? {
        return sessionAccessToken
    }

    override suspend fun saveAccessToken(
        accessToken: String,
        persist: Boolean,
    ) {
        sessionAccessToken = accessToken

        if (persist) {
            preferences.edit()
                .putString(
                    ACCESS_TOKEN_KEY,
                    accessToken,
                )
                .apply()
        } else {
            preferences.edit()
                .remove(ACCESS_TOKEN_KEY)
                .apply()
        }
    }

    override suspend fun clear() {
        sessionAccessToken = null

        preferences.edit()
            .remove(ACCESS_TOKEN_KEY)
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "oshu_session"
        const val ACCESS_TOKEN_KEY = "access_token"
    }
}

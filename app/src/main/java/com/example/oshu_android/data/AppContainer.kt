package com.example.oshu_android.data

import android.content.Context
import com.example.oshu_android.data.auth.AuthModule
import com.example.oshu_android.data.auth.LoginRepository
import com.example.oshu_android.data.auth.LoginRepositoryImpl
import com.example.oshu_android.data.auth.LoginResponse
import com.example.oshu_android.data.auth.SessionStore
import com.example.oshu_android.feature.onboarding.OnboardingPreferences

class AppContainer(
    context: Context,
) {
    private val applicationContext =
        context.applicationContext

    private val sessionStore: SessionStore =
        MemorySessionStore()

    val onboardingPreferences =
        OnboardingPreferences(
            context = applicationContext,
        )

    val loginRepository: LoginRepository =
        LoginRepositoryImpl(
            authApi = AuthModule.provideAuthApi(
                applicationContext
            ),
            sessionStore = sessionStore,
        )
}

private class MemorySessionStore : SessionStore {
    private var currentSession:
            LoginResponse? = null

    override suspend fun save(
        response: LoginResponse,
        persist: Boolean,
    ) {
        currentSession = response
    }
}
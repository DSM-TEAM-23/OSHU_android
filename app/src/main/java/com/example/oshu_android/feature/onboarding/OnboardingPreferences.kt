package com.example.oshu_android.feature.onboarding

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "onboarding_preferences",
)

class OnboardingPreferences(
    context: Context,
) {
    private val dataStore = context.applicationContext.onboardingDataStore

    val isCompleted: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[IS_COMPLETED] ?: false
        }

    suspend fun setCompleted() {
        dataStore.edit { preferences ->
            preferences[IS_COMPLETED] = true
        }
    }

    suspend fun reset() {
        dataStore.edit { preferences ->
            preferences.remove(IS_COMPLETED)
        }
    }

    private companion object {
        val IS_COMPLETED = booleanPreferencesKey(
            "is_onboarding_completed",
        )
    }
}
package com.ezhart.todotxtandroid.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "app_settings"

class SettingsStorage(private val context: Context) {

    private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

    private object PreferencesKeys {
        val ACCOUNT_DISPLAY_NAME = stringPreferencesKey("account_display_name")
        val ACCOUNT_EMAIL = stringPreferencesKey("account_email")
    }

    val accountDisplayName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ACCOUNT_DISPLAY_NAME] ?: ""
    }

    val accountEmail: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ACCOUNT_EMAIL] ?: ""
    }

    suspend fun setAccountDisplayName(accountDisplayName: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCOUNT_DISPLAY_NAME] = accountDisplayName
        }
    }

    suspend fun setAccountEmail(accountEmail: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCOUNT_EMAIL] = accountEmail
        }
    }
}

class SettingsRepository(
    private val settingsStorage: SettingsStorage
) {
    val accountDisplayName: Flow<String> = settingsStorage.accountDisplayName
    val accountEmail: Flow<String> = settingsStorage.accountEmail

    suspend fun setAccountDisplayName(accountDisplayName: String) {
        settingsStorage.setAccountDisplayName(accountDisplayName)
    }

    suspend fun setAccountEmail(accountEmail: String) {
        settingsStorage.setAccountEmail(accountEmail)
    }
}


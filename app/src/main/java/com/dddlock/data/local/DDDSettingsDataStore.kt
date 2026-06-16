package com.dddlock.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
private val Context.dataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(
    name = "dddlock_settings"
)

class DDDSettingsDataStore(private val context: Context) {

    private object Keys {
        val BLOCKED_DDDS = stringSetPreferencesKey("blocked_ddds")
        val BLOCKER_ENABLED = booleanPreferencesKey("blocker_enabled")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val USE_DYNAMIC_COLORS = booleanPreferencesKey("use_dynamic_colors")
    }

    val blockedDDDs: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[Keys.BLOCKED_DDDS] ?: emptySet()
    }

    val isBlockerEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[Keys.BLOCKER_ENABLED] ?: false
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[Keys.DARK_MODE] ?: false
    }

    val useDynamicColors: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[Keys.USE_DYNAMIC_COLORS] ?: true
    }

    suspend fun setBlockedDDDs(ddds: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[Keys.BLOCKED_DDDS] = ddds
        }
    }

    suspend fun setBlockerEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.BLOCKER_ENABLED] = enabled
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.DARK_MODE] = enabled
        }
    }

    suspend fun setUseDynamicColors(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.USE_DYNAMIC_COLORS] = enabled
        }
    }

    suspend fun getBlockedDDDsOnce(): Set<String> {
        return context.dataStore.data.first()[Keys.BLOCKED_DDDS] ?: emptySet()
    }

    suspend fun isBlockerEnabledOnce(): Boolean {
        return context.dataStore.data.first()[Keys.BLOCKER_ENABLED] ?: false
    }
}

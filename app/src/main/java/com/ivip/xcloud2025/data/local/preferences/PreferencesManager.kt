package com.ivip.xcloudtv2025.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Extensão para criar DataStore de preferências
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "xcloud_settings")

/**
 * Classe para gerenciar configurações do aplicativo usando DataStore (sem Hilt)
 */
class PreferencesManager(private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        // Chaves básicas
        private val PLAYLIST_URL = stringPreferencesKey("playlist_url")
        private val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val USERNAME = stringPreferencesKey("username")
        private val PASSWORD = stringPreferencesKey("password")
        private val USE_AUTHENTICATION = booleanPreferencesKey("use_authentication")
        private val LAST_WATCHED_CHANNEL_ID = longPreferencesKey("last_watched_channel_id")

        // Valores padrão
        const val DEFAULT_THEME_MODE = "DARK"
    }

    suspend fun setPlaylistUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[PLAYLIST_URL] = url
        }
    }

    fun getPlaylistUrl(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[PLAYLIST_URL] ?: ""
        }
    }

    suspend fun setCredentials(username: String, password: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME] = username
            preferences[PASSWORD] = password
        }
    }

    fun getUsername(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USERNAME] ?: ""
        }
    }

    fun getPassword(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[PASSWORD] ?: ""
        }
    }

    suspend fun setUseAuthentication(useAuth: Boolean) {
        dataStore.edit { preferences ->
            preferences[USE_AUTHENTICATION] = useAuth
        }
    }

    fun getUseAuthentication(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[USE_AUTHENTICATION] ?: false
        }
    }

    suspend fun setLastWatchedChannelId(channelId: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_WATCHED_CHANNEL_ID] = channelId
        }
    }

    fun getLastWatchedChannelId(): Flow<Long> {
        return dataStore.data.map { preferences ->
            preferences[LAST_WATCHED_CHANNEL_ID] ?: 0L
        }
    }

    suspend fun setFirstLaunch(isFirstLaunch: Boolean) {
        dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH] = isFirstLaunch
        }
    }

    fun isFirstLaunch(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[FIRST_LAUNCH] ?: true
        }
    }

    suspend fun clearAllSettings() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
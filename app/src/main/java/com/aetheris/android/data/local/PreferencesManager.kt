package com.aetheris.android.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.aetheris.android.data.model.AppSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aetheris_prefs")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // Keys
    private val tokenKey = stringPreferencesKey("auth_token")
    private val serverUrlKey = stringPreferencesKey("server_url")
    private val userIdKey = stringPreferencesKey("user_id")
    private val userNameKey = stringPreferencesKey("user_name")
    private val userEmailKey = stringPreferencesKey("user_email")
    private val darkModeKey = booleanPreferencesKey("dark_mode")
    private val notificationsKey = booleanPreferencesKey("notifications")
    private val lanDiscoveryKey = booleanPreferencesKey("lan_discovery")
    private val languageKey = stringPreferencesKey("language")
    private val refreshIntervalKey = intPreferencesKey("refresh_interval")
    private val lastServerIdKey = stringPreferencesKey("last_server_id")

    val serverUrl: Flow<String> = context.dataStore.data.map { it[serverUrlKey] ?: "https://aetheris-panel.vercel.app/" }
    val token: Flow<String?> = context.dataStore.data.map { it[tokenKey] }
    val userId: Flow<String?> = context.dataStore.data.map { it[userIdKey] }
    val userName: Flow<String?> = context.dataStore.data.map { it[userNameKey] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[userEmailKey] }
    val darkMode: Flow<Boolean> = context.dataStore.data.map { it[darkModeKey] ?: true }
    val notifications: Flow<Boolean> = context.dataStore.data.map { it[notificationsKey] ?: true }
    val lanDiscovery: Flow<Boolean> = context.dataStore.data.map { it[lanDiscoveryKey] ?: true }
    val language: Flow<String> = context.dataStore.data.map { it[languageKey] ?: "en" }
    val refreshInterval: Flow<Int> = context.dataStore.data.map { it[refreshIntervalKey] ?: 30 }
    val lastServerId: Flow<String?> = context.dataStore.data.map { it[lastServerIdKey] }

    suspend fun saveAuth(token: String, userId: String, name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[tokenKey] = token
            prefs[userIdKey] = userId
            prefs[userNameKey] = name
            prefs[userEmailKey] = email
        }
    }

    suspend fun saveServerUrl(url: String) {
        context.dataStore.edit { it[serverUrlKey] = url }
    }

    suspend fun clearAuth() {
        context.dataStore.edit { prefs ->
            prefs.remove(tokenKey)
            prefs.remove(userIdKey)
            prefs.remove(userNameKey)
            prefs.remove(userEmailKey)
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[darkModeKey] = enabled }
    }

    suspend fun setNotifications(enabled: Boolean) {
        context.dataStore.edit { it[notificationsKey] = enabled }
    }

    suspend fun setLanDiscovery(enabled: Boolean) {
        context.dataStore.edit { it[lanDiscoveryKey] = enabled }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[languageKey] = lang }
    }

    suspend fun setRefreshInterval(seconds: Int) {
        context.dataStore.edit { it[refreshIntervalKey] = seconds }
    }

    suspend fun setLastServerId(id: String) {
        context.dataStore.edit { it[lastServerIdKey] = id }
    }

    suspend fun getSettings(): AppSettings {
        return AppSettings(
            serverUrl = "https://aetheris-panel.vercel.app/",
            autoConnect = true,
            lanDiscovery = true,
            notifications = true,
            darkMode = true,
            language = "en",
            refreshInterval = 30
        )
    }
}

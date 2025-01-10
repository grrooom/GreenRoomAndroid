package green.room.preference

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object DevicePreference {
    private const val TAG = "DevicePreference"
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "device_preference")

    enum class PreferenceKey(val key: String) {
        ONBOARDING_COMPLETED("onboarding_completed"),
        NICK_NAME("nick_name")
    }

    /**
     * Save a string value to the DataStore
     */
    suspend fun saveString(context: Context, preference: PreferenceKey, value: String) {
        val preferencesKey = androidx.datastore.preferences.core.stringPreferencesKey(preference.key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
        logAllValues(context)
    }


    /**
     * Save a boolean value to the DataStore
     */
    suspend fun saveBoolean(context: Context, preference: PreferenceKey, value: Boolean) {
        val preferencesKey = booleanPreferencesKey(preference.key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
        logAllValues(context)
    }

    /**
     * Retrieve a string value from the DataStore
     */
    fun getString(context: Context, preference: PreferenceKey, defaultValue: String): Flow<String> {
        val preferencesKey = androidx.datastore.preferences.core.stringPreferencesKey(preference.key)
        return context.dataStore.data.map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }
    }

    /**
     * Retrieve a boolean value from the DataStore
     */
    fun getBoolean(context: Context, preference: PreferenceKey, defaultValue: Boolean): Flow<Boolean> {
        val preferencesKey = booleanPreferencesKey(preference.key)
        return context.dataStore.data.map { preferences ->
            val value = preferences[preferencesKey] ?: defaultValue
            logAllValues(context)
            value
        }
    }

    /**
     * Delete a boolean value from the DataStore
     */
    suspend fun deleteBoolean(context: Context, preference: PreferenceKey) {
        val preferencesKey = booleanPreferencesKey(preference.key)
        context.dataStore.edit { preferences ->
            preferences.remove(preferencesKey)
        }
        logAllValues(context)
    }

    /**
     * Clear all values from the DataStore
     */
    suspend fun clearAll(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.clear() // 모든 값을 지움
        }
        // clear 후에 로그 출력
        logAllValues(context)
    }

    /**
     * Log all values in the DataStore
     */
    private suspend fun logAllValues(context: Context) {
        val preferences = context.dataStore.data.first() // 현재 저장된 모든 값을 가져옴
        val allValues = preferences.asMap().map { entry ->
            "*${entry.key.name} = ${entry.value}"
        }.joinToString(separator = "\n")
        Log.d(TAG, "Current DataStore values:\n[\n$allValues\n]")
    }
}

package green.room.preference

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 *  * =========================================================
 *  * 🔐 EncryptedSharedPreferences Data Flow (Sequence Diagram)
 *  * =========================================================
 *
    User                           App                          Android Keystore              Disk
    │                              │                                  │                           │
    │ ─────── 1. Create `MasterKey` ────> │                                  │                           │
    │                              │ ─────── 2. Store encryption key in Keystore ───────> │                           │
    │                              │                                  │                           │
    │ ─────── 3. Initialize `EncryptedSharedPreferences` ───> │                                  │                           │
    │                              │ ─────── 4. Allocate file for encrypted storage ───> │                           │
    │                              │                                  │                           │
    │ ─────── 5. Store data (`putString()`) ───> │                                  │                           │
    │                              │ ─────── 6. Encrypt data using AES-256-GCM ───> │                           │
    │                              │                                  │ ─────── 7. Store encrypted data ───> │
    │                              │                                  │                           │
    │ ─────── 8. Retrieve data (`getString()`) ───> │                                  │                           │
    │                              │ ─────── 9. Decrypt data using AES-256-GCM ───> │                           │
    │                              │                                  │ ─────── 10. Return decrypted data ───> │
    │                              │                                  │                           │
    │ ─────── 11. Delete data (`remove()`) ───> │                                  │                           │
    │                              │ ─────── 12. Remove encrypted data from storage ───> │                           │
    │                              │                                  │                           │
    │ ─────── 13. Clear all (`clear()`) ───> │                                  │                           │
    │                              │ ─────── 14. Remove all encrypted data ───> │                           │
    │                              │                                  │                           │
 */
@Module
@InstallIn(SingletonComponent::class)
object SecurePreferencesModule {

    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}

@Singleton
class DevicePreference @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    private val TAG = "DevicePreference"

    enum class PreferenceKey(val key: String) {
        ONBOARDING_COMPLETED("onboarding_completed"),
        EMAIL("email"),
        NICK_NAME("nick_name"),
        ACCESS_TOKEN("access_token"),
        REFRESH_TOKEN("refresh_token")
    }

    /**
     * Save a string value to EncryptedSharedPreferences
     */
    fun saveString(preference: PreferenceKey, value: String) {
        sharedPreferences.edit().putString(preference.key, value).apply()
        logAllValues()
    }

    /**
     * Save a boolean value to EncryptedSharedPreferences
     */
    fun saveBoolean(preference: PreferenceKey, value: Boolean) {
        sharedPreferences.edit().putBoolean(preference.key, value).apply()
        logAllValues()
    }

    /**
     * Retrieve a string value from EncryptedSharedPreferences
     */
    fun getString(preference: PreferenceKey, defaultValue: String): String {
        return sharedPreferences.getString(preference.key, defaultValue) ?: defaultValue
    }

    /**
     * Retrieve a boolean value from EncryptedSharedPreferences
     */
    fun getBoolean(preference: PreferenceKey, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(preference.key, defaultValue)
    }

    /**
     * Delete a specific value from EncryptedSharedPreferences
     */
    fun deleteValue(preference: PreferenceKey) {
        sharedPreferences.edit().remove(preference.key).apply()
        logAllValues()
    }

    /**
     * Clear all values from EncryptedSharedPreferences
     */
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
        logAllValues()
    }

    /**
     * Log all values in EncryptedSharedPreferences
     */
    private fun logAllValues() {
        val allValues = sharedPreferences.all.map { entry ->
            "*${entry.key} = ${entry.value}"
        }.joinToString(separator = "\n")
        Log.d(TAG, "Current EncryptedSharedPreferences values:\n[\n$allValues\n]")
    }
}

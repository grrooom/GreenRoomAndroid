package green.room.common.network.interceptor

import green.room.common.DevicePreference
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages authentication for API requests by handling token expiration and automatic re-authentication.
 **/
@Singleton
class TokenManager @Inject constructor(
    private val preference: DevicePreference
) {
    fun getAuthAccessToken(): String {
        return preference.getString(DevicePreference.PreferenceKey.ACCESS_TOKEN, "")
    }

    fun saveAuthAccessToken(newAccessToken: String) {
        return preference.saveString(DevicePreference.PreferenceKey.ACCESS_TOKEN, newAccessToken)
    }

    fun getAuthRefreshToken(): String {
        return preference.getString(DevicePreference.PreferenceKey.REFRESH_TOKEN, "")
    }

    fun saveAuthRefreshToken(newRefreshToken: String) {
        return preference.saveString(DevicePreference.PreferenceKey.REFRESH_TOKEN, newRefreshToken)
    }

    private companion object {
        const val TAG = "TokenManager"
    }
}

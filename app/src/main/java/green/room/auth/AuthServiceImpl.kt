package green.room.auth

import android.util.Log
import green.room.BuildConfig
import green.room.common.network.ApiEndpoints
import green.room.common.network.StandardAPIResponse
import retrofit2.Response
import javax.inject.Inject

class AuthServiceImpl @Inject constructor(
    private val signupService: AuthService
) {
    private val TAG = "AuthServiceImpl"

    suspend fun putEmailAuth(email: String): Response<StandardAPIResponse<Unit>> {
        return try {
            val res = signupService.putEmailAuth(email)
            Log.d(TAG, "${BuildConfig.BASE_URL + ApiEndpoints.PUT_AUTH_EMAIL_AUTHENTICATION} API Response = $res")
            res
        } catch (e: Exception) {
            Log.e(TAG, "Network request failed", e)
            throw e
        }
    }
}

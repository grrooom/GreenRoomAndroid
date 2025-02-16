package green.room.common.network.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor
    (private val tokenManager: TokenManager) : Interceptor {
        val TAG = "AuthInterceptor"
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = tokenManager.getAuthAccessToken()

        val precedingRequest = originalRequest.newBuilder()
            .apply {
                if (originalRequest.header("Authorization").isNullOrEmpty()) {
                    header("Authorization","Bearer $accessToken")
                    Log.d(TAG, "Making authorized header : Bearer $accessToken")
                }
            }
            .build()

        return chain.proceed(precedingRequest)
    }
}
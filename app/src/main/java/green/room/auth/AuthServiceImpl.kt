package green.room.auth

import android.util.Log
import green.room.common.network.ApiResult
import green.room.common.network.StandardAPIResponse
import retrofit2.Response
import javax.inject.Inject

class AuthServiceImpl @Inject constructor(
    private val authService: AuthService
) {
    private val TAG = "AuthServiceImpl"
    private suspend fun <T> handleResponse(
        apiCall: suspend () -> Response<StandardAPIResponse<T>>,
        successCode: Int = 200
    ): ApiResult<T?> {
        return try {
            val response = apiCall()
            val body = response.body()

            when {
                response.isSuccessful && response.code() == successCode -> {
                    Log.v(TAG, "HTTP ${response.code()} Success")
                    if (successCode == 204) {
                        @Suppress("UNCHECKED_CAST")
                        ApiResult.Success(Unit as T)
                    } else {
                        body?.data?.let { ApiResult.Success(it) } ?: ApiResult.Error("NULL_DATA", "Response data is null")
                    }
                }
                response.isSuccessful -> {
                    Log.e(TAG, "Error from server: ${body?.code}, ${body?.status}")
                    ApiResult.Error(body?.code ?: "UNKNOWN", body?.status ?: "Unknown error")
                }
                else -> {
                    Log.e(TAG, "HTTP error: ${response.code()}, ${response.message()}")
                    ApiResult.Error(response.code().toString(), response.message())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network request failed", e)
            ApiResult.NetworkError
        }
    }

    suspend fun sendEmailToken(email: String): ApiResult<Unit?> =
        handleResponse(
            apiCall = { authService.putSendEmailToken(email) },
            successCode = 204
        )

    suspend fun validateEmailToken(token: String): ApiResult<Unit?> =
        handleResponse(
            apiCall = { authService.putValidateEmailToken(token) },
            successCode = 204
        )

    suspend fun reqSignup(reqSignupBody: AuthService.ReqSignupBody): ApiResult<AuthService.ResSignupBody?> =
        handleResponse(
            apiCall = { authService.postSignup(reqSignupBody) },
            successCode = 201
        )
}

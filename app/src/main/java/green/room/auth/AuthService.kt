package green.room.auth

import green.room.common.network.ApiEndpoints
import green.room.common.network.StandardAPIResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

data class EmailAuthRequest(val redirectUrl: String, val email: String)

interface AuthService {
    @PUT(ApiEndpoints.PUT_AUTH_EMAIL_AUTHENTICATION)
    suspend fun putEmailAuth(@Body request: String): Response<StandardAPIResponse<Unit>>
}

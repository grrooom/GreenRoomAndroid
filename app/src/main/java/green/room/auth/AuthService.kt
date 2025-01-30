package green.room.auth

import green.room.common.network.ApiEndpoints
import green.room.common.network.StandardAPIResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthService {
    @PUT(ApiEndpoints.EMAIL_AUTHENTICATION)
    suspend fun putSendEmailToken(@Body email: String): Response<StandardAPIResponse<Unit>>

    @PUT(ApiEndpoints.EMAIL_TOKEN_AUTHENTICATION)
    suspend fun putValidateEmailToken(@Body token: String): Response<StandardAPIResponse<Unit>>

    @POST(ApiEndpoints.SIGN_UP)
    suspend fun postSignup(@Body reqSignupBody: ReqSignupBody): Response<StandardAPIResponse<ResSignupBody>>

    data class ReqSignupBody(
        val email: String,
        val password: String,
        val name: String
    )

    data class ResSignupBody(
        val email: String,
        val accessToken: String,
        val refreshToken: String
    )
}

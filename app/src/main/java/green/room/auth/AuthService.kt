package green.room.auth

import green.room.common.network.APIEndpoints
import green.room.common.network.ServerStandardResponseFormat
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthService {
    @PUT(APIEndpoints.EMAIL_AUTHENTICATION)
    suspend fun putSendEmailToken(@Body email: String): Response<ServerStandardResponseFormat<Unit>>

    @PUT(APIEndpoints.EMAIL_TOKEN_AUTHENTICATION)
    suspend fun putValidateEmailToken(@Body token: String): Response<ServerStandardResponseFormat<Unit>>

    @POST(APIEndpoints.SIGN_UP)
    suspend fun postSignup(@Body reqSignupBody: ReqSignupBody): Response<ServerStandardResponseFormat<ResSignupBody>>

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

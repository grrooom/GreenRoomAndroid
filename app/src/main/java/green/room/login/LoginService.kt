package green.room.login

import green.room.common.network.APIEndpoints
import green.room.common.network.ServerStandardResponseFormat
import green.room.common.network.interceptor.NetworkConstants
import green.room.login.model.PostLoginRequestDTO
import green.room.login.model.PostLoginResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginService {

    @POST(APIEndpoints.LOGIN)
    // The Auth interceptor is not required for the post-login endpoint.
    @Headers("${NetworkConstants.SKIP_AUTH_INTERCEPTOR_HEADER}: ${NetworkConstants.SKIP_AUTH_INTERCEPTOR_TRUE}")
    suspend fun postLogin(@Body postLoginRequestDTO: PostLoginRequestDTO): Response<ServerStandardResponseFormat<PostLoginResponseDTO>>
}

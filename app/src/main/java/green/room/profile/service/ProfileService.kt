package green.room.profile.service

import green.room.common.network.ApiEndpoints
import green.room.common.network.StandardAPIResponseBody
import green.room.profile.model.ProfileDTO
import retrofit2.Response
import retrofit2.http.GET

interface ProfileService {
    @GET(ApiEndpoints.PROFILE_INFORMATION)
    suspend fun getProfile(): Response<StandardAPIResponseBody<ProfileDTO>>
}

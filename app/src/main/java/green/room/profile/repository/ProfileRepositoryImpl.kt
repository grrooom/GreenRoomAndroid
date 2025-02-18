package green.room.profile.repository

import green.room.common.network.ApiResult
import green.room.profile.model.ProfileDTO
import green.room.profile.service.ProfileService
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileService: ProfileService
): ProfileRepository {
    override suspend fun getProfile(): ApiResult<ProfileDTO> {
        return try {
            val response = profileService.getProfile()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.data != null) {
                    ApiResult.Success(body.data)
                } else {
                    ApiResult.Error("NULL_DATA", "Response data is null")
                }
            } else {
                ApiResult.Error(response.code().toString(), response.message())
            }
        } catch (e: Exception) {
            ApiResult.NetworkError
        }
    }

}

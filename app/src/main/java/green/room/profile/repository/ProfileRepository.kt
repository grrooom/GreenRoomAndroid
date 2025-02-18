package green.room.profile.repository

import green.room.common.network.ApiResult
import green.room.profile.model.ProfileDTO

interface ProfileRepository {
    suspend fun getProfile(): ApiResult<ProfileDTO>
}

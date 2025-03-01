package green.room.profile

import dagger.Lazy
import green.room.common.network.ApiResult
import green.room.common.network.SafeApiExecutor
import green.room.profile.model.ProfileDTO
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileService: Lazy<ProfileService>,
    private val safeApiExecutor: SafeApiExecutor
): ProfileRepository {

    override suspend fun getProfile(): ApiResult<ProfileDTO> {
        return safeApiExecutor.safeApiCall { profileService.get().getProfile() }
    }
}

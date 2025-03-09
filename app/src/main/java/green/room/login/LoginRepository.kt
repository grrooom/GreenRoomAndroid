package green.room.login

import green.room.common.network.ApiResult
import green.room.login.model.PostLoginRequestDTO
import green.room.login.model.PostLoginResponseDTO

interface LoginRepository {
    suspend fun login(postLoginRequestDTO: PostLoginRequestDTO): ApiResult<PostLoginResponseDTO>
}

package green.room.login

import dagger.hilt.android.scopes.ViewModelScoped
import green.room.common.network.APIHandler
import green.room.common.network.ApiResult
import green.room.common.network.interceptor.TokenManager
import green.room.login.model.PostLoginRequestDTO
import green.room.login.model.PostLoginResponseDTO
import javax.inject.Inject

@ViewModelScoped
class LoginRepositoryImpl @Inject constructor
    (
    private val loginService: LoginService,
    private val apiHandler: APIHandler,
    private val tokenManager: TokenManager
) : LoginRepository {

    override suspend fun login(postLoginRequestDTO: PostLoginRequestDTO): ApiResult<PostLoginResponseDTO> {
        val result = apiHandler.catchErrorApiCall {
            loginService.postLogin(postLoginRequestDTO)
        }
        when (result) {
            is ApiResult.Success -> {
                tokenManager.saveAuthAccessToken(result.data.accessToken)
                tokenManager.saveAuthRefreshToken(result.data.refreshToken)
            }
            is ApiResult.Error -> {

            }
            is ApiResult.NetworkError -> {

            }
        }
        return result
    }
}

package green.room.login.binds

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import green.room.common.network.APIHandler
import green.room.common.network.interceptor.TokenManager
import green.room.login.LoginRepository
import green.room.login.LoginRepositoryImpl
import green.room.login.LoginService

@Module
@InstallIn(ViewModelComponent::class)
object LoginRepositoryModule {

    @Provides
    fun provideLoginRepository(loginService: LoginService, apiHandler: APIHandler, tokenManager: TokenManager): LoginRepository {
        return LoginRepositoryImpl(loginService, apiHandler, tokenManager)
    }
}

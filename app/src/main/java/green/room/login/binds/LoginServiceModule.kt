package green.room.login.binds

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import green.room.login.LoginService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginServiceModule {

    @Provides
    @Singleton
    fun provideLoginService(retrofit: Retrofit) : LoginService {
        return retrofit.create(LoginService::class.java)
    }
}

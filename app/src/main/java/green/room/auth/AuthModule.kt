package green.room.auth

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthServiceImpl(authService: AuthService): AuthServiceImpl {
        return AuthServiceImpl(authService)
    }
}

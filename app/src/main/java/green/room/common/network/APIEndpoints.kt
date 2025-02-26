package green.room.common.network

import green.room.BuildConfig

object APIEndpoints {
    const val EMAIL_AUTHENTICATION = BuildConfig.BASE_URL + "/api/auth/email/authentication"
    const val EMAIL_TOKEN_AUTHENTICATION = BuildConfig.BASE_URL + "/api/auth/email/token/authentication"
    const val SIGN_UP = BuildConfig.BASE_URL + "/api/auth/signup"
    const val REISSUE_USER_TOKENS = BuildConfig.BASE_URL + "/api/auth/tokens" // Renew client access, refresh tokens
    const val HOME_INFORMATION = BuildConfig.BASE_URL + "/api/greenroom/info"
    const val PROFILE_INFORMATION = BuildConfig.BASE_URL + "/api/users/info"
    const val PROFILE_IMAGE_UPDATE = BuildConfig.BASE_URL + "/api/users/profile-image"
}

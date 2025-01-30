package green.room.common.network

import green.room.BuildConfig

object ApiEndpoints {
    const val EMAIL_AUTHENTICATION = BuildConfig.BASE_URL + "/api/auth/email/authentication"
    const val EMAIL_TOKEN_AUTHENTICATION = BuildConfig.BASE_URL + "/api/auth/email/token/authentication"
    const val SIGN_UP = BuildConfig.BASE_URL + "/api/auth/signup"
}

package green.room.common.network

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val code: String, val message: String? = null) : ApiResult<Nothing>()
    object NetworkError : ApiResult<Nothing>()
}

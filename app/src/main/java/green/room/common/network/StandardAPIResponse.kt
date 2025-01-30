package green.room.common.network

// The server must always return responses in this structure when using REST API.
data class StandardAPIResponse<T>(
    val status: String,
    val code: String,
    val data: T?
)

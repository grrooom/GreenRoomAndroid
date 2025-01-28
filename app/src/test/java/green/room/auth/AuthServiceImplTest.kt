package green.room.auth

import green.room.common.network.StandardAPIResponse
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import retrofit2.Response

class AuthServiceImplTest {

    @Mock
    private lateinit var mockAuthService: AuthService

    private lateinit var authServiceImpl: AuthServiceImpl

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Before
    fun setup() {
        authServiceImpl = AuthServiceImpl(mockAuthService)
    }

    @Test
    fun `putEmailAuth should return success response`() = runBlocking {
        val redirectUrl = "https://example.com"
        val email = "test@example.com"
        val mockResponse = Response.success(
            StandardAPIResponse<Unit>(
                status = "OK",
                code = "A001",
                data = null
            )
        )

        `when`(mockAuthService.putEmailAuth(EmailAuthRequest(redirectUrl, email))).thenReturn(mockResponse)

        val response = authServiceImpl.putEmailAuth(redirectUrl, email)

        assert(response.isSuccessful)
        assert(response.body()?.status == "OK")
        assert(response.body()?.code == "A001")
        verify(mockAuthService).putEmailAuth(EmailAuthRequest(redirectUrl, email))
    }

    @Test
    fun `putEmailAuth should handle BAD_REQUEST response`() = runBlocking {
        val redirectUrl = "https://example.com"
        val email = "test@example.com"
        val mockResponse = Response.error<StandardAPIResponse<Unit>>(
            400,
            """
            {
              "status": "BAD_REQUEST",
              "code": "C025",
              "data": null
            }
        """.trimIndent().toResponseBody("application/json".toMediaType())
        )

        `when`(mockAuthService.putEmailAuth(EmailAuthRequest(redirectUrl, email))).thenReturn(mockResponse)

        val response = authServiceImpl.putEmailAuth(redirectUrl, email)

        assert(!response.isSuccessful)
        assert(response.code() == 400)
        verify(mockAuthService).putEmailAuth(EmailAuthRequest(redirectUrl, email))
    }

    @Test
    fun `putEmailAuth should handle CONFLICT response`() = runBlocking {
        val redirectUrl = "https://example.com"
        val email = "test@example.com"
        val mockResponse = Response.error<StandardAPIResponse<Unit>>(
            409,
            """
            {
              "status": "CONFLICT",
              "code": "C022",
              "data": null
            }
        """.trimIndent().toResponseBody("application/json".toMediaType())
        )

        `when`(mockAuthService.putEmailAuth(EmailAuthRequest(redirectUrl, email))).thenReturn(mockResponse)

        val response = authServiceImpl.putEmailAuth(redirectUrl, email)

        assert(!response.isSuccessful)
        assert(response.code() == 409)
        verify(mockAuthService).putEmailAuth(EmailAuthRequest(redirectUrl, email))
    }

    @Test
    fun `putEmailAuth should handle INTERNAL_SERVER_ERROR response`() = runBlocking {
        val redirectUrl = "https://example.com"
        val email = "test@example.com"
        val mockResponse = Response.error<StandardAPIResponse<Unit>>(
            500,
            """
            {
              "status": "INTERNAL_SERVER_ERROR",
              "code": "D002",
              "data": null
            }
        """.trimIndent().toResponseBody("application/json".toMediaType())
        )

        `when`(mockAuthService.putEmailAuth(EmailAuthRequest(redirectUrl, email))).thenReturn(mockResponse)

        val response = authServiceImpl.putEmailAuth(redirectUrl, email)

        assert(!response.isSuccessful)
        assert(response.code() == 500)
        verify(mockAuthService).putEmailAuth(EmailAuthRequest(redirectUrl, email))
    }
}

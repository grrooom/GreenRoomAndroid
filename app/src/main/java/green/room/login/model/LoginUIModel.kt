package green.room.login.model

import green.room.common.converter.DTOConvertible
import javax.annotation.concurrent.Immutable

@Immutable
data class LoginUIModel(
    val loginId: String,
    val password: String,
    val loginStatus: LoginStatus
): DTOConvertible<PostLoginRequestDTO> {
    enum class LoginStatus {
        IDLE,
        REQUEST_PENDING,
        COMPLETE,
        ERROR
    }

    override fun toDTO(): PostLoginRequestDTO {
        return PostLoginRequestDTO(
            email = loginId,
            password = password
        )
    }
}

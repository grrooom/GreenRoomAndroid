package green.room.profile.model

import javax.annotation.concurrent.Immutable

@Immutable
data class ProfileUIModel(
    val userName: String,
    val email: String,
    val userDurationWithGreenroom: Int,
    val level: Int,
    val levelName: String,
    val profileImageUrl: String?,
    val seedsToNextLevel: Int
)

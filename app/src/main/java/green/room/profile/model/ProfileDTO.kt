package green.room.profile.model

import com.google.gson.annotations.SerializedName
import green.room.common.converter.UIModelConvertible
import javax.annotation.concurrent.Immutable

@Immutable
data class ProfileDTO(
    @SerializedName("userName") val userName: String,
    @SerializedName("email") val email: String,
    @SerializedName("userDurationWithGreenroom") val userDurationWithGreenroom: Int,
    @SerializedName("level") val level: Int,
    @SerializedName("levelName") val levelName: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String?,
    @SerializedName("seedsToNextLevel") val seedsToNextLevel: Int
) : UIModelConvertible<ProfileUIModel> {

    override fun toUIModel(): ProfileUIModel =
        ProfileUIModel(
            userName = userName,
            email = email,
            userDurationWithGreenroom = userDurationWithGreenroom,
            level = level,
            levelName = levelName,
            profileImageUrl = profileImageUrl,
            seedsToNextLevel = seedsToNextLevel
        )
}

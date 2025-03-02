package green.room.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import dagger.hilt.android.AndroidEntryPoint
import green.room.R
import green.room.common.GlideApp
import green.room.mainpage.MainPageActivity
import green.room.notification.NotificationActivity
import green.room.profile_edit.ProfileEditActivity

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private val vM : ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val profileBackButton: Toolbar = findViewById(R.id.profile_appbar)

        val profileImage: ImageView = findViewById(R.id.profile_image)
        val profileName: TextView = findViewById(R.id.profile_name)
        val profileLevel: TextView = findViewById(R.id.profile_level)

        val profileEditIcon: ImageView = findViewById(R.id.profile_edit_icon)
        val logoutText: TextView = findViewById(R.id.profile_log_out)
        val pushSetup: TextView = findViewById(R.id.profile_setting_push_notifications)

        vM.profileUIModel.observe(this) { model ->
            model.profileImageUrl?.let { url ->
               GlideApp.with(this)
                   .load(url)
                   .circleCrop()
                   .into(profileImage)
            }

            profileName.text = model.userName
            profileLevel.text = getString(R.string.profile_level_format, model.levelName, model.level)
        }

        profileBackButton.setOnClickListener {
            val intent = Intent(this, MainPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        // Set click listeners
        setIconClickNavListener(profileImage, ProfileEditActivity::class.java)
        setIconClickNavListener(profileEditIcon, ProfileEditActivity::class.java)
        setTextClickNavListener(pushSetup, NotificationActivity::class.java)
        logoutText.setOnClickListener {
            showLogoutDialog()
        }
    }

    // Helper function to set click listeners
    private fun setIconClickNavListener(view: ImageView, destination: Class<*>) {
        view.setOnClickListener {
            toProfileEditPage(
                profileImageUrl = vM.profileUIModel.value?.profileImageUrl,
                nickName = vM.profileUIModel.value?.userName,
                destination = destination)
        }
    }

    private fun setTextClickNavListener(view: TextView, destination: Class<*>) {
        view.setOnClickListener {
            toProfileEditPage(
                profileImageUrl = vM.profileUIModel.value?.profileImageUrl,
                nickName = vM.profileUIModel.value?.userName,
                destination = destination)
        }
    }

    private fun toProfileEditPage(profileImageUrl: String?, nickName :String? ,destination: Class<*>) {
        val intent = Intent(this, destination)
        intent.putExtra(PROFILE_IMAGE_URL, profileImageUrl)
        intent.putExtra(NICK_NAME, nickName)
        startActivity(intent)
    }

    private fun showLogoutDialog() {
        val dialog = MaterialDialog(this).customView(R.layout.logout_dialog)

        val dialogView = dialog.getCustomView()
        val cancelButton = dialogView.findViewById<Button>(R.id.dialog_cancel)
        val confirmButton = dialogView.findViewById<Button>(R.id.dialog_confirm)

        cancelButton.setOnClickListener { dialog.dismiss() }
        confirmButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.cornerRadius(16f)

        dialog.show()
    }

    companion object {
       const val PROFILE_IMAGE_URL = "profileImageUrl"
       const val NICK_NAME = "nickName"
    }

}

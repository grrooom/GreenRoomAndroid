package green.room.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.viewModelScope
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import dagger.hilt.android.AndroidEntryPoint
import green.room.R
import green.room.mainpage.MainPageActivity
import green.room.notification.NotificationActivity
import green.room.profileedit.ProfileEditActivity
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private val vM : ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        vM.viewModelScope.launch {
            vM.getProfile()
        }

        val profileBackButton: Toolbar = findViewById(R.id.profile_appbar)
        val profileImage: ImageView = findViewById(R.id.profile_image)
        val profileEditIcon: ImageView = findViewById(R.id.profile_edit_icon)
        val logoutText: TextView = findViewById(R.id.profile_log_out)

        val pushSetup: TextView = findViewById(R.id.profile_setting_push_notifications)

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
            startActivity(Intent(this, destination))
        }
    }

    private fun setTextClickNavListener(view: TextView, destination: Class<*>) {
        view.setOnClickListener {
            startActivity(Intent(this, destination))
        }
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

}

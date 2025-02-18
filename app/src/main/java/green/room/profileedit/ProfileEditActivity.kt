package green.room.profileedit

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import green.room.R
import green.room.common.permisson.PermissionManager
import green.room.profile.ProfileActivity
import green.room.common.ui.BottomSheetFragment
import javax.inject.Inject

@AndroidEntryPoint
class ProfileEditActivity : AppCompatActivity(), BottomSheetFragment.BottomSheetListener {
    private val vM: ProfileEditViewModel by viewModels()

    @Inject
    lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        val profileEditBackButton : Toolbar = findViewById(R.id.profile_edit_appbar)
        val imageView = findViewById<ImageView>(R.id.profile_edit_image)
        val textInput = findViewById<EditText>(R.id.profile_edit_nickname_input)
        textInput.addTextChangedListener(vM.textWatcher)

        val confirmButton = findViewById<Button>(R.id.profile_edit_confirm_button)

        profileEditBackButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        vM.textInput.observe(this) { newText ->
            val drawable = confirmButton.background as? GradientDrawable
            drawable?.setColor(
                ContextCompat.getColor(
                    this,
                    if (newText.isNullOrEmpty()) R.color.standard_gray else R.color.standard_green
                )
            )
        }

        vM.imageUri.observe(this) { uri ->
            uri?.let { imageView.setImageURI(it) }
        }

        vM.hasReadImagesPermission.observe(this) { isGranted ->
            if (isGranted) {
                pickImage()
            }
        }

        imageView.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
    }

    override fun onPickImageClick() {
        checkPermissionAndPickImage()
    }

    private fun checkPermissionAndPickImage() {
        if (permissionManager.hasReadImagesPermission()) {
            pickImage()
        } else {
            permissionManager.requestReadImagesPermission(requestImagePermissionLauncher)
        }
    }

    private val requestImagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        vM.updatePermissionStatus()
        if (!isGranted) {
            Toast.makeText(
                this,
                "Can't access images without READ_MEDIA_IMAGES permission",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun pickImage() {
        val pickIntent = permissionManager.getPickImageIntent()
        pickImageLauncher.launch(pickIntent)
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val selectedImageUri = result.data?.data
            vM.setImageUri(selectedImageUri)
        }
    }
}

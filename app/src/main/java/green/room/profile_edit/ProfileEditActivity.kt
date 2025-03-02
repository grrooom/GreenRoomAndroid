package green.room.profile_edit

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import green.room.R
import green.room.common.permisson.PermissionManager
import green.room.common.ui.BottomSheetFragment
import green.room.profile.ProfileActivity
import javax.inject.Inject

@AndroidEntryPoint
class ProfileEditActivity : AppCompatActivity(), BottomSheetFragment.BottomSheetListener {
    private val vM: ProfileEditViewModel by viewModels()

    @Inject
    lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Intent에서 프로필 이미지 URL과 닉네임 추출
        val intent = intent
        val profileImage = intent.getStringExtra(ProfileActivity.PROFILE_IMAGE_URL)
        val nickName = intent.getStringExtra(ProfileActivity.NICK_NAME)

        setContentView(R.layout.activity_profile_edit)

        // 뷰 바인딩
        val profileEditBackButton: Toolbar = findViewById(R.id.profile_edit_appbar)
        val imageView = findViewById<ImageView>(R.id.profile_edit_image)
        val textInput = findViewById<EditText>(R.id.profile_edit_nickname_input)
        val confirmButton = findViewById<Button>(R.id.profile_edit_confirm_button)

        // 닉네임 초기값 설정
        nickName?.let { textInput.setText(it) }
        // 프로필 이미지 URL이 있다면 기본 이미지 URI로 ViewModel에 반영
        profileImage?.let { vM.setDefaultImageUri(Uri.parse(it)) }

        // 텍스트 변경 감지 (닉네임 업데이트)
        textInput.addTextChangedListener(vM.textWatcher)

        // 뒤로가기 버튼 클릭 시 ProfileActivity로 이동
        profileEditBackButton.setOnClickListener {
            val backIntent = Intent(this, ProfileActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            }
            startActivity(backIntent)
        }

        // ViewModel의 ProfileEditUIModel을 관찰하여 UI 업데이트
        vM.profileEditUIModel.observe(this) { model ->
            // 닉네임 값에 따라 확인 버튼 색상 업데이트
            val drawable = confirmButton.background as? GradientDrawable
            drawable?.setColor(
                ContextCompat.getColor(
                    this,
                    if (model.nickName?.isEmpty() == true) R.color.standard_gray else R.color.standard_green
                )
            )

            // 이미지 뷰 업데이트: updatedImageURI가 있다면 그걸, 그렇지 않으면 기본 이미지 사용
            if (model.updatedImageURI != null && model.updatedImageURI != Uri.EMPTY) {
                imageView.setImageURI(model.updatedImageURI)
            } else if (model.defaultImageURI != Uri.EMPTY) {
                Glide.with(this)
                    .load(model.defaultImageURI)
                    .circleCrop()
                    .placeholder(R.drawable.ic_rounded_profile)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView)
            }
        }

        // 이미지 클릭 시 BottomSheetFragment를 표시하여 이미지 선택 옵션 제공
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

    // 권한 요청 결과 처리 – 실제 권한 상태를 확인하여 ALWAYS_ALLOW, LIMITED_ALLOW, DENIED로 분기 처리
    private val requestImagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Android 버전에 따른 권한 상수
        val permission = android.Manifest.permission.READ_MEDIA_IMAGES

        when (permissionManager.getGalleryPermissionStatus()) {
            PermissionManager.GalleryPermissionStatus.ALWAYS_ALLOW -> {
                vM.updatePermissionStatus()
                pickImage()
            }
            PermissionManager.GalleryPermissionStatus.LIMITED_ALLOW -> {
                Toast.makeText(this, "갤러리 접근 권한이 제한적으로 허용되었습니다.", Toast.LENGTH_SHORT).show()
                vM.updatePermissionStatus()
                pickImage()
            }
            PermissionManager.GalleryPermissionStatus.DENIED -> {
                if (!shouldShowRequestPermissionRationale(permission)) {
                    Toast.makeText(
                        this,
                        "앱 설정에서 갤러리 접근 권한을 허용해 주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                    val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                    startActivity(settingsIntent)
                } else {
                    checkPermissionAndPickImage()
                }
            }
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
            vM.setUpdatedImageUri(selectedImageUri)
        }
    }

    companion object {
        const val TAG = "ProfileEditActivity"
    }
}

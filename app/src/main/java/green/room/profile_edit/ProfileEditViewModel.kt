package green.room.profile_edit

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import green.room.common.permisson.PermissionManager
import green.room.profile_edit.model.ProfileEditUIModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val uploadProfileImageUseCase: UploadProfileImageUseCase,
    private val permissionManager: PermissionManager
) : ViewModel() {
    private val _profileEditUIModel = MutableLiveData<ProfileEditUIModel>().apply {
        value = ProfileEditUIModel(
            defaultImageURI = Uri.EMPTY,
            updatedImageURI = null,
            nickName = "",
            hasGalleryPermission = permissionManager.hasReadImagesPermission()
        )
    }
    val profileEditUIModel: LiveData<ProfileEditUIModel> = _profileEditUIModel

    val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            _profileEditUIModel.value = _profileEditUIModel.value?.copy(
                nickName = s?.toString() ?: ""
            )
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    fun updatePermissionStatus() {
        _profileEditUIModel.value = _profileEditUIModel.value?.copy(
            hasGalleryPermission = permissionManager.hasReadImagesPermission()
        )
    }

    fun setUpdatedImageUri(uri: Uri?) {
        _profileEditUIModel.value = _profileEditUIModel.value?.copy(
            updatedImageURI = uri
        )
        viewModelScope.launch {
            _profileEditUIModel.value?.let { uploadProfileImageUseCase(it) }
        }
    }

    fun setDefaultImageUri(uri: Uri) {
        _profileEditUIModel.value = _profileEditUIModel.value?.copy(
            defaultImageURI = uri
        )
    }
}

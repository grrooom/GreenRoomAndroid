package green.room.profileedit

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import green.room.common.permisson.PermissionManager
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    private val _textInput = MutableLiveData<String?>()
    val textInput: LiveData<String?> = _textInput

    val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            _textInput.value = s?.toString() ?: ""
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private val _hasReadImagesPermission = MutableLiveData<Boolean>()
    val hasReadImagesPermission: LiveData<Boolean> = _hasReadImagesPermission

    fun updatePermissionStatus() {
        _hasReadImagesPermission.value = permissionManager.hasReadImagesPermission()
    }

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }
}

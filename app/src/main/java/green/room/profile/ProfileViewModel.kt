package green.room.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import green.room.common.network.ApiResult
import green.room.profile.model.ProfileUIModel
import green.room.profile.usecase.GetProfileUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {
    val TAG = "ProfileViewModel"

    private val _profileUIModel = MutableLiveData<ProfileUIModel>()
    val profileUIModel: LiveData<ProfileUIModel> = _profileUIModel

    init {

    }

    fun getProfile() {
        viewModelScope.launch {
            when(val result = getProfileUseCase()) {
                is ApiResult.Success -> {
                    _profileUIModel.value = result.data.toUIModel()
                }

                is ApiResult.Error -> {

                }

                is ApiResult.NetworkError -> {

                }
            }
        }
    }
}

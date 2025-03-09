package green.room.login.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import green.room.common.network.ApiResult
import green.room.login.PostLoginUseCase
import green.room.login.model.LoginUIModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginFragmentViewModel @Inject constructor(
    private val postLoginUseCase: PostLoginUseCase
) : ViewModel() {

    val loginId = MutableLiveData("")
    val password = MutableLiveData("")

    private val _loginStatus = MutableLiveData(LoginUIModel.LoginStatus.IDLE)
    val loginStatus: LiveData<LoginUIModel.LoginStatus> get() = _loginStatus

    val isFieldsNotEmpty = MediatorLiveData<Boolean>().apply {
        addSource(loginId) { validateInputs() }
        addSource(password) { validateInputs() }
    }

    private fun validateInputs() {
        val isValid = !loginId.value.isNullOrBlank() && !password.value.isNullOrBlank()
        isFieldsNotEmpty.value = isValid
    }

    fun performLogin() {
        Log.d(TAG, "Performing Login with ID: ${loginId.value}, PW: ${password.value}")

        val id = loginId.value ?: ""
        val pwd = password.value ?: ""

        if (id.isNotBlank() && pwd.isNotBlank()) {
            _loginStatus.value = LoginUIModel.LoginStatus.REQUEST_PENDING

            // Make UI Model to convey value to useCase layer
            val uiModel = LoginUIModel(loginId = id, password = pwd, loginStatus = _loginStatus.value!!)

            viewModelScope.launch {
                val result = postLoginUseCase.invoke(uiModel)

                when (result) {
                    is ApiResult.Success -> {
                        _loginStatus.value = LoginUIModel.LoginStatus.COMPLETE
                    }
                    is ApiResult.Error -> {
                        _loginStatus.value = LoginUIModel.LoginStatus.ERROR
                    }
                    is ApiResult.NetworkError -> {
                        _loginStatus.value = LoginUIModel.LoginStatus.ERROR
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "LoginFragmentViewModel"
    }
}

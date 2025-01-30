package green.room.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import green.room.common.network.ApiResult
import green.room.preference.DevicePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authServiceImpl: AuthServiceImpl,
    private val devicePreference: DevicePreference
) : ViewModel() {

    private val TAG = "AuthViewModel"

    enum class AuthStep {
        EMAIL_NOT_AUTHENTICATED,
        EMAIL_PENDING_CONFIRMATION,
        NICKNAME,
        PASSWORD,
        SIGN_UP_DONE
    }

    private val _currentAuthStep = MutableLiveData(AuthStep.EMAIL_NOT_AUTHENTICATED)
    val currentAuthStep: LiveData<AuthStep> get() = _currentAuthStep

    fun setCurrentStep(authStep: AuthStep) {
        _currentAuthStep.value = authStep
    }

    private val _topInstructionText = MutableLiveData<String>()
    val topInstructionText: LiveData<String> get() = _topInstructionText

    private val _bottomInstructionText = MutableLiveData<String>()
    val bottomInstructionText: LiveData<String> get() = _bottomInstructionText

    private var isCountdownActive = false
    private val _timerText = MutableLiveData("5:00")
    val timerText: LiveData<String> get() = _timerText

    private val _isConfirmButtonEnabled = MutableLiveData(false)
    val isConfirmButtonEnabled: LiveData<Boolean> get() = _isConfirmButtonEnabled

    init {
        changeSequence()
    }

    fun updateInputText(inputText: String) {
        _isConfirmButtonEnabled.value = inputText.isNotBlank()
    }

    fun confirmInput(inputText: String) {
        viewModelScope.launch {
            if (inputText.isNotBlank()) {
                when (_currentAuthStep.value) {
                    AuthStep.EMAIL_NOT_AUTHENTICATED -> {
                        authServiceImpl.sendEmailToken(inputText)
                        Log.v(TAG, "Send verification token at : $inputText")
                        _currentAuthStep.value = AuthStep.EMAIL_PENDING_CONFIRMATION
                        startCountdown()
                        devicePreference.saveString(DevicePreference.PreferenceKey.EMAIL, inputText)
                    }

                    AuthStep.NICKNAME -> {
                        devicePreference.saveString(
                            DevicePreference.PreferenceKey.NICK_NAME,
                            inputText
                        )
                        _currentAuthStep.value = AuthStep.PASSWORD
                    }

                    AuthStep.PASSWORD -> {
                        val email =
                            devicePreference.getString(DevicePreference.PreferenceKey.EMAIL, "")
                        val nickName =
                            devicePreference.getString(DevicePreference.PreferenceKey.NICK_NAME, "")

                        val reqSignupBody = AuthService.ReqSignupBody(
                            email = email,
                            password = inputText,
                            name = nickName
                        )

                        val result = authServiceImpl.reqSignup(reqSignupBody)
                        if (result is ApiResult.Success) {
                            result.data?.accessToken?.let {
                                devicePreference.saveString(
                                    DevicePreference.PreferenceKey.ACCESS_TOKEN,
                                    result.data.accessToken
                                )

                            }

                            result.data?.refreshToken.let {
                                result.data?.let { it1 ->
                                    devicePreference.saveString(
                                        DevicePreference.PreferenceKey.REFRESH_TOKEN,
                                        it1.refreshToken
                                    )
                                }
                            }
                        } else {
                            Log.e(TAG, "Sign up failed : ${(result as? ApiResult.Error)?.message}")
                        }
                        setCurrentStep(AuthStep.SIGN_UP_DONE)
                    }
                    else -> {}
                }
                changeSequence()
            }
        }
    }

    fun validateEmailToken(token: String) {
        viewModelScope.launch {
            Log.v(TAG, "Verify Email verification token : $token")
            runCatching {
                authServiceImpl.validateEmailToken(token)
            }.onSuccess { result ->
                when (result) {
                    is ApiResult.Success -> {
                        Log.v(TAG, "Token validated successfully")
                        setCurrentStep(AuthStep.NICKNAME)
                        changeSequence()
                    }

                    is ApiResult.Error -> {
                        isCountdownActive = false
                        _timerText.value = "인증 코드가 올바르지 않습니다. 인증 코드를 재발급 해주세요."
                        Log.e(TAG, "Error validating token: ${result.message}")
                    }

                    is ApiResult.NetworkError -> {
                        isCountdownActive = false
                        _timerText.value = "네트워크 오류 발생. 다시 시도해주세요."
                        Log.e(TAG, "Network error occurred")
                    }
                }
            }.onFailure { throwable ->
                Log.e(TAG, "Unexpected error: ", throwable)
            }
        }
    }

    private fun changeSequence() {
        Log.v(TAG, "Client Auth Sequence = ${_currentAuthStep.value}")
        when (_currentAuthStep.value) {
            AuthStep.EMAIL_NOT_AUTHENTICATED -> {
                _topInstructionText.value = "이메일 인증을 진행합니다"
                _bottomInstructionText.value = "이메일을 입력해주세요"
            }

            AuthStep.EMAIL_PENDING_CONFIRMATION -> {
                _bottomInstructionText.value = "인증 코드가 발급되었습니다. 이메일을 확인해주세요."
            }

            AuthStep.NICKNAME -> {
                _topInstructionText.value = "닉네임을 설정해주세요"
                _bottomInstructionText.value = "사용할 닉네임을 입력해주세요"
            }

            AuthStep.PASSWORD -> {
                _topInstructionText.value = "비밀번호를 설정해주세요"
                _bottomInstructionText.value = "사용할 비밀번호를 입력해주세요."
            }

            else -> {}
        }
    }

    private fun startCountdown() {
        viewModelScope.launch(Dispatchers.Main) {
            isCountdownActive = true
            var remainingTime = 5 * 60
            while (remainingTime >= 0 && isCountdownActive) {
                val minutes = remainingTime / 60
                val seconds = remainingTime % 60
                _timerText.value = String.format("%d:%02d", minutes, seconds)
                delay(1000L)
                remainingTime--
            }
            if (isCountdownActive) {
                _bottomInstructionText.value = "이메일 인증 시간이 초과되었습니다. 다시 시도해주세요."
            }
        }
    }
}

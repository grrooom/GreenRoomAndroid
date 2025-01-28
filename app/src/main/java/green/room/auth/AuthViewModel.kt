package green.room.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import green.room.preference.DevicePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
     private val authService: AuthService
) : ViewModel() {

    private val TAG = "AuthViewModel"

    enum class AuthStep {
        EMAIL_VERIFICATION,
        EMAIL_PENDING_CONFIRMATION,
        EMAIL_COMPLETED,
        NICKNAME_SETUP
    }

    private val _currentAuthStep = MutableLiveData(AuthStep.EMAIL_VERIFICATION)
    val currentAuthStep: LiveData<AuthStep> get() = _currentAuthStep

    fun setCurrentStep(authStep: AuthStep) {
        _currentAuthStep.value = authStep
    }

    private val _headerText = MutableLiveData<String>()
    val topInstructionText: LiveData<String> get() = _headerText

    private val _headerSubText = MutableLiveData<String>()
    val bottomInstructionText: LiveData<String> get() = _headerSubText

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
                    AuthStep.EMAIL_VERIFICATION -> {
                        startEmailVerification(inputText)
                        _currentAuthStep.value = AuthStep.EMAIL_PENDING_CONFIRMATION
                        startCountdown()
                    }
                    AuthStep.NICKNAME_SETUP -> {
                        saveNickname(inputText)
                    }
                    else -> {}
                }
                changeSequence()
            }
        }
    }

    private suspend fun startEmailVerification(email: String) {
        Log.v(TAG, "Request email authentication with : $email")
        authService.putEmailAuth(email)
    }

    private fun saveNickname(nickname: String) {
        viewModelScope.launch(Dispatchers.IO) {
            DevicePreference.saveString(
                context = null, // Update with appropriate context handling
                preference = DevicePreference.PreferenceKey.NICK_NAME,
                value = nickname
            )
            Log.v(TAG, "닉네임 저장 완료: $nickname")
        }
    }

    private fun changeSequence() {
        when (_currentAuthStep.value) {
            AuthStep.EMAIL_VERIFICATION -> {
                _headerText.value = "이메일 인증을 진행합니다"
                _headerSubText.value = "이메일을 입력해주세요"
            }
            AuthStep.EMAIL_PENDING_CONFIRMATION -> {
                _headerSubText.value = "인증 코드가 발급되었습니다. 이메일을 확인해주세요."
            }
            AuthStep.NICKNAME_SETUP -> {
                _headerText.value = "닉네임을 설정해주세요"
                _headerSubText.value = "사용할 닉네임을 입력해주세요"
            }
            else -> {}
        }
    }

    private fun startCountdown() {
        viewModelScope.launch(Dispatchers.Main) {
            var remainingTime = 5 * 60
            while (remainingTime >= 0) {
                val minutes = remainingTime / 60
                val seconds = remainingTime % 60
                _timerText.value = String.format("%d:%02d", minutes, seconds)
                delay(1000L)
                remainingTime--
            }
            _headerSubText.value = "이메일 인증 시간이 초과되었습니다. 다시 시도해주세요."
        }
    }
}

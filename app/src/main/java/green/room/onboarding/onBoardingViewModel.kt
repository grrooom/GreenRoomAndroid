package green.room.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import green.room.preference.DevicePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val devicePreference: DevicePreference
) : ViewModel() {
    fun setOnboadingDoneOnPref() {
        viewModelScope.launch(Dispatchers.IO) {
            devicePreference.saveBoolean(DevicePreference.PreferenceKey.ONBOARDING_COMPLETED, true)
        }
    }
}
package green.room

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import dagger.hilt.android.AndroidEntryPoint
import green.room.notification.MessagingService
import green.room.onboarding.OnboardingActivity
import green.room.preference.DevicePreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var devicePreference: DevicePreference
    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics

        val messagingService = MessagingService()
        messagingService.getToken()

        Log.i("MainActivity", "Fetching FCM token...")

        CoroutineScope(Dispatchers.IO).launch {
            if (BuildConfig.DEBUG) {
                devicePreference.clearAll()
            }
        }
        moveToOnboarding()
    }


    private fun moveToOnboarding() {
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
        finish()
    }
}

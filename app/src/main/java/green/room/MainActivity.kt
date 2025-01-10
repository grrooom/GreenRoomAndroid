package green.room

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import green.room.onboarding.OnboardingActivity
import green.room.preference.DevicePreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            DevicePreference.clearAll(this@MainActivity)
        }

        // OnboardingActivity로 이동
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)


        // MainActivity 종료
        finish()
    }
}
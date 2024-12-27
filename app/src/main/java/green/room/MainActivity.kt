package green.room

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import green.room.onboarding.OnboardingActivity

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // OnboardingActivity로 이동
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)

        // MainActivity 종료
        finish()
    }
}
package green.room.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import green.room.R
import green.room.common.route.LoginExtras
import green.room.common.route.LoginExtrasKey
import green.room.login.fragment.LoginFragment

@AndroidEntryPoint
class LoginActivity  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var fragment = LoginFragment()

        val extraValue = intent.getStringExtra(LoginExtrasKey.VALUE.key)?.let { value ->
            LoginExtras.entries.find { it.value == value }
        }

        if (extraValue != null) {
           fragment = LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(LoginExtrasKey.VALUE.key, extraValue.value)
                }
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}

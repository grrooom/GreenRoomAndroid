package green.room.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import green.room.R
import green.room.mainpage.MainPageActivity

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    val TAG = "AuthActivity"
    private val viewModel: AuthViewModel by viewModels()
    private var timerTextView: TextView? = null
    private var timerStub: ViewStub? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle deep link if activity is launched via intent
        if (isDeepLinkAccess(intent)) {
            handleDeepLink(intent)
        }

        setContentView(R.layout.activity_auth)

        val topInstructionText = findViewById<TextView>(R.id.signup_head_3)
        val bottomInstructionText = findViewById<TextInputLayout>(R.id.signup_body_2)
        timerStub = findViewById(R.id.timer_stub)
        val inputField = findViewById<EditText>(R.id.name_input)
        val confirmButton = findViewById<Button>(R.id.signup_confirm_button)

        timerStub?.visibility = View.GONE
        timerTextView?.visibility = View.GONE

        viewModel.setCurrentStep(AuthViewModel.AuthStep.EMAIL_NOT_AUTHENTICATED)

        viewModel.currentAuthStep.observe(this) { step ->
            if (step == AuthViewModel.AuthStep.EMAIL_PENDING_CONFIRMATION) {
                if (timerTextView == null) {
                    timerStub?.inflate()
                    timerTextView = findViewById(R.id.time_display)
                }
                // Make timerTextView visible only in EMAIL_VERIFICATION step
                timerTextView?.visibility = View.VISIBLE
            } else {
                // Hide timerTextView in all other steps
                timerTextView?.visibility = View.GONE
            }
            if (viewModel.currentAuthStep.value == AuthViewModel.AuthStep.NICKNAME) {
                inputField.setText("")
            }

            if (viewModel.currentAuthStep.value == AuthViewModel.AuthStep.PASSWORD) {
                Log.v(TAG, "input type should be encrypted")
                inputField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                inputField.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
            }

            if (viewModel.currentAuthStep.value == AuthViewModel.AuthStep.SIGN_UP_DONE) {
                val intent = Intent(this, MainPageActivity::class.java)
                startActivity(intent)
            }
        }

        viewModel.topInstructionText.observe(this) { text ->
            topInstructionText.text = text
        }

        viewModel.bottomInstructionText.observe(this) { text ->
            bottomInstructionText.helperText = text
        }

        viewModel.timerText.observe(this) { time ->
            timerTextView?.text = time
        }

        viewModel.isConfirmButtonEnabled.observe(this) { isEnabled ->
            confirmButton.isEnabled = isEnabled
            confirmButton.setBackgroundColor(
                if (isEnabled)
                    resources.getColor(R.color.standard_green, theme)
                else resources.getColor(R.color.standard_gray, theme)
            )
        }

        inputField.addTextChangedListener {
            viewModel.updateInputText(it.toString())
        }

        confirmButton.setOnClickListener {
            viewModel.confirmInput(inputField.text.toString())
            if (viewModel.currentAuthStep.value != AuthViewModel.AuthStep.EMAIL_NOT_AUTHENTICATED) {
                inputField.setText("")
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        Log.d(TAG, "Foreground running app called from deeplink url")
        super.onNewIntent(intent)
        // Handle new intent when activity is already running
        handleDeepLink(intent)
    }

    private fun isDeepLinkAccess(intent: Intent?): Boolean {
        val data: Uri? = intent?.data
        return data != null // If data is not null, it's a deep link
    }

    private fun handleDeepLink(intent: Intent?) {
        val data: Uri = intent?.data ?: run {
            Log.w(TAG, "No deep link data received")
            return
        }

        val token = data.getQueryParameter("token").orEmpty()
        if (token.isNotBlank()) {
            Log.d(TAG, "Deep Link Triggered: $data")
            Log.d(TAG, "Passed token from deep link: $token")
            viewModel.validateEmailToken(token)
        } else {
            Log.w(TAG, "Token is missing in the deep link")
        }
    }
}

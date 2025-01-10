package green.room.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import green.room.R
import green.room.preference.DevicePreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    private var currentStep = Step.EMAIL_VERIFICATION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val confirmButton = findViewById<Button>(R.id.signup_confirm_button)
        val inputField = findViewById<TextInputEditText>(R.id.input_field) // Input field
        val inputLayout = findViewById<TextInputLayout>(R.id.signup_body_2) // Input layout
        val headerTextView = findViewById<TextView>(R.id.signup_head_3)
        val progressBar = findViewById<CircularProgressIndicator>(R.id.progress_bar)

        updateUIForCurrentStep(headerTextView, inputLayout, inputField)

        // 입력 필드 변화에 따른 버튼 활성화
        inputField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isInputValid = !s.isNullOrBlank()
                confirmButton.setBackgroundColor(
                    if (isInputValid)
                        resources.getColor(R.color.standard_green, theme)
                    else resources.getColor(R.color.standard_gray, theme)
                )
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 버튼 클릭 동작
        confirmButton.setOnClickListener {
            val inputText = inputField.text.toString()
            if (inputText.isNotBlank()) {
                when (currentStep) {
                    Step.EMAIL_VERIFICATION -> {
                        // 이메일 인증 처리
                        startEmailVerification(inputText)
                        currentStep = Step.NICKNAME_SETUP

                        updateUIForCurrentStep(headerTextView, inputLayout, inputField)
                    }
                    Step.NICKNAME_SETUP -> {
                        // 닉네임 설정 처리
                        CoroutineScope(Dispatchers.IO).launch {
                            DevicePreference.saveString(
                                context = this@SignupActivity,
                                preference = DevicePreference.PreferenceKey.NICK_NAME,
                                value = inputText
                            )
                        }

                        // UI 업데이트
                        headerTextView.text = "닉네임 설정 완료"
                        inputLayout.helperText = "회원가입이 완료되었습니다"
                        inputField.setText("")
                        inputField.hint = ""
                        confirmButton.setBackgroundColor(resources.getColor(R.color.standard_gray, theme))
                        confirmButton.isEnabled = false // 버튼 비활성화
                    }
                }
            }
        }
    }

    private fun updateUIForCurrentStep(
        headerTextView: TextView,
        inputLayout: TextInputLayout,
        inputField: TextInputEditText
    ) {
        when (currentStep) {
            Step.EMAIL_VERIFICATION -> {
                headerTextView.text = "이메일 인증을 진행합니다"
                inputLayout.helperText = "이메일을 입력해주세요"
                inputField.hint = "greenRoom@gmail.com"
                inputField.setText("")
            }
            Step.NICKNAME_SETUP -> {
                headerTextView.text = "닉네임을 설정해주세요"
                inputLayout.helperText = "사용할 닉네임을 입력해주세요"
                inputField.hint = "닉네임"
                inputField.setText("")
            }
        }
    }

    private fun startEmailVerification(email: String) {
        // 이메일 인증 로직 추가 (이메일 전송, 인증 확인 등)
        Log.d("SignupActivity", "이메일 인증 요청: $email")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // 딥링크 데이터 확인
        val data = intent.data
        if (data != null) {
            val scheme = data.scheme // https
            val host = data.host // greenroom.com
            val path = data.path // /signup
            val key = data.getQueryParameter("key") // value
            val userId = data.getQueryParameter("userId") // 123

            // 로그 출력
            Log.d("onNewIntent", "- Scheme: $scheme, Host: $host, Path: $path, Key: $key, UserId: $userId")
        }
    }

    private enum class Step {
        EMAIL_VERIFICATION,
        NICKNAME_SETUP
    }
}

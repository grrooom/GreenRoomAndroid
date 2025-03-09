package green.room.login.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import green.room.R
import green.room.auth.AuthActivity
import green.room.common.route.LoginExtras
import green.room.common.route.LoginExtrasKey
import green.room.databinding.FragmentLoginBinding
import green.room.login.model.LoginUIModel
import green.room.mainpage.MainPageWithBottomNavigationBarActivity

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val vM: LoginFragmentViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var needHideBackButton: LoginExtras? = null
    override fun onAttach(context: Context) {
        Log.d(TAG, "$TAG attached to Activity")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        needHideBackButton = arguments?.getString(LoginExtrasKey.VALUE.key)?.let { value ->
            LoginExtras.entries.find { it.value == value }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.viewModel = vM
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (needHideBackButton == LoginExtras.HIDE_HEADER_BACK_BUTTON) {
            binding.loginBackButton.visibility = View.GONE
        }

        binding.loginBackButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.loginWasNotSignUp.setOnClickListener {
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        vM.isFieldsNotEmpty.observe(viewLifecycleOwner) { isEnabled ->
            val colorRes = if (isEnabled) R.color.standard_green else R.color.standard_gray
            binding.loginConfirmButton.setBackgroundColor(
                ContextCompat.getColor(requireContext(), colorRes)
            )
        }

        vM.loginStatus.observe(viewLifecycleOwner) {
            if (it == LoginUIModel.LoginStatus.COMPLETE) {
                val intent = Intent(requireContext(), MainPageWithBottomNavigationBarActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "LoginFragment"
    }
}

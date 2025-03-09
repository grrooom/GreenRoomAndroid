package green.room.login.binds

import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import green.room.R
import green.room.login.model.LoginUIModel

object LoginBindingAdapters {
    @JvmStatic
    @BindingAdapter("loginStatusColor")
    fun setLoginStatusColor(button: Button, status: LoginUIModel.LoginStatus?) {
        status?.let {
            val colorRes = when (it) {
                LoginUIModel.LoginStatus.IDLE -> R.color.standard_gray
                LoginUIModel.LoginStatus.REQUEST_PENDING -> R.color.gray100
                LoginUIModel.LoginStatus.COMPLETE -> R.color.standard_green
                LoginUIModel.LoginStatus.ERROR -> R.color.standard_red
            }
            button.setBackgroundColor(ContextCompat.getColor(button.context, colorRes))
        }
    }
}

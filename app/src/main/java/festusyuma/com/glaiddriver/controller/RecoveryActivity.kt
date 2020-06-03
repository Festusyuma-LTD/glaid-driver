package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.Window
import android.view.WindowManager
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.utilities.EXTRA_RECOVERY_TYPE
import kotlinx.android.synthetic.main.activity_recovery.*

class RecoveryActivity : AppCompatActivity() {
    //    val messageView : String by lazy {
//        // runs on first access of messageView
//        findViewById(R.id.message_view) as TextView
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery)
        val w: Window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        w.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        //
        println("EXTRA_RECOVERY_TYPE " + intent.getStringExtra(EXTRA_RECOVERY_TYPE))
        when (intent.getStringExtra(EXTRA_RECOVERY_TYPE)) {
            "phone" -> {
                recovery_text.setText(R.string.phone_recovery_text)
                labelText.setText(R.string.phone_number)
                editTextType.hint = getString(R.string.phone_number)
                editTextType.inputType = InputType.TYPE_CLASS_PHONE
                handleSubmitBtn.text = "Send OTP"
                resendOtpText.visibility = View.INVISIBLE
                resendOtpbtn.visibility = View.INVISIBLE
            }
            "email" -> {
                recovery_text.setText(R.string.email_recovery_text)
                labelText.setText(R.string.email_address)
                editTextType.hint = getString(R.string.email_address)
                editTextType.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                handleSubmitBtn.text = "Send Mail"
                resendOtpText.visibility = View.INVISIBLE
                resendOtpbtn.visibility = View.INVISIBLE

            }
            "OTP" -> {
                recovery_text.setText(R.string.otp_recovery_text)
                labelText.setText(R.string.otp)
                editTextType.hint = getString(R.string.otp)
                editTextType.inputType = InputType.TYPE_CLASS_NUMBER
                handleSubmitBtn.setText(R.string.continue_text)
                resendOtpText.visibility = View.VISIBLE
                resendOtpbtn.visibility = View.VISIBLE

            }
            else -> {
            }
        }
    }

    fun handleSubmitBtnClick(view: View) {
        when (intent.getStringExtra(EXTRA_RECOVERY_TYPE)) {
            "phone" -> {
                val signUpIntent = Intent(this, RecoveryActivity::class.java)
                signUpIntent.putExtra(EXTRA_RECOVERY_TYPE, "OTP")
                startActivity(signUpIntent)
            }
            "email" -> {
                val signUpIntent = Intent(this, ResetPasswordActivity::class.java)
                startActivity(signUpIntent)
            }
            "OTP" -> {
                val signUpIntent = Intent(this, ResetPasswordActivity::class.java)
                startActivity(signUpIntent)
            }
            else -> {
            }

        }

    }

    fun resendOtpClick(view: View) {

    }
}

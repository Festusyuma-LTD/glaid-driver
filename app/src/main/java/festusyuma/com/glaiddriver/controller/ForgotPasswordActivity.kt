package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.helpers.EXTRA_FORGOT_PASSWORD_CHOICE
import festusyuma.com.glaiddriver.helpers.EXTRA_RECOVERY_TYPE
import festusyuma.com.glaiddriver.helpers.buttonClickAnim
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {
    private var otpChoice : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        if (Build.VERSION.SDK_INT >= VERSION_CODES.O) window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        radioGroupForgotPassword.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.emailRadio){
                otpChoice = "email"
            } else if (checkedId == R.id.telRadio){
                otpChoice = "phone"
            }
        }
    }

    fun getOtpTypeMethod(view: View) {
        view.startAnimation(buttonClickAnim)
        if (otpChoice != "") {
            val getOtpIntent = Intent(this, RecoveryActivity::class.java)
            getOtpIntent.putExtra(EXTRA_FORGOT_PASSWORD_CHOICE, otpChoice)
            startActivity(getOtpIntent)
        } else {
            Toast.makeText(applicationContext, "please select one of the options", Toast.LENGTH_LONG).show()
        }
    }
}

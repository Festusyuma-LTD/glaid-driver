package festusyuma.com.glaiddriver.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import festusyuma.com.glaiddriver.R

class SignupOtpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_otp)
    }

    fun otpSignupBtnClick(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun resendOtpClick(view: View) {}
}

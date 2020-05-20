package festusyuma.com.glaiddriver.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import festusyuma.com.glaiddriver.R

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }

    fun toggleRePasswordClick(view: View) {

    }

    fun togglePasswordClick(view: View) {

    }
    fun submitSignUpBtnClick(view: View) {
        val intent = Intent(this, SignupOtpActivity::class.java)
        startActivity(intent)
    }
}

package festusyuma.com.glaiddriver.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import festusyuma.com.glaiddriver.R

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
    }

    fun togglePassword2Click(view: View) {

    }

    fun togglePasswordClick(view: View) {

    }

    fun resetBtnClick(view: View) {
        val signInIntent = Intent(this, LoginActivity::class.java)
        startActivity(signInIntent)

    }
}

package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import festusyuma.com.glaiddriver.R

class ProblemsLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problems_login)
        val w: Window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        w.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
    }
    fun reSigninbtnClick(view: View){
        val signInIntent = Intent(this, LoginActivity::class.java)
        startActivity(signInIntent)

    }

    fun forgotPasswordBtnClick(view: View){
        val signUpIntent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(signUpIntent)

    }

    fun forgotEmailBtnClick(view: View){

    }
}

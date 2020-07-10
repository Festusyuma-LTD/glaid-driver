package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.helpers.buttonClickAnim

class ProblemsLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problems_login)
    }
    fun reSigninbtnClick(view: View){
        view.startAnimation(buttonClickAnim)
        val signInIntent = Intent(this, LoginActivity::class.java)
        startActivity(signInIntent)

    }

    fun forgotPasswordBtnClick(view: View){
        view.startAnimation(buttonClickAnim)
        val signUpIntent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(signUpIntent)

    }

    fun forgotEmailBtnClick(view: View){
        view.startAnimation(buttonClickAnim)

    }
}

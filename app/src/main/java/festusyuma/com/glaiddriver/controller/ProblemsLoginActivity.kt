package festusyuma.com.glaiddriver.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import festusyuma.com.glaiddriver.R

class ProblemsLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problems_login)
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

package festusyuma.com.glaiddriver.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import festusyuma.com.glaiddriver.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
    fun togglePasswordClick(view: View){

    }
    fun loginBtnClick(view: View){

    }
    fun forgotPasswordClick(view: View){
        val signUpIntent = Intent(this, ProblemsLoginActivity::class.java)
        startActivity(signUpIntent)

    }
}

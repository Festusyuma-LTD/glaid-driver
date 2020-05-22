package festusyuma.com.glaiddriver.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import festusyuma.com.glaiddriver.R

class GetStartedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)
    }
    fun emailSignUpBtnClick(view: View){
        println("view $view")
        val signUpIntent = Intent(this, SignUpActivity::class.java)
        startActivity(signUpIntent)

    }
    fun fbSignUpBtnClick(view: View){

    }
    fun googleSignUpBtnClick(view: View){

    }
    fun getStartedSignInBtnClick(view: View){
        val signInIntent = Intent(this, LoginActivity::class.java)
        startActivity(signInIntent)

    }
    fun termOfUseClick(view: View){

    }
}

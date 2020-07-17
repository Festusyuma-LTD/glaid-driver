package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.utilities.PrefManager
import festusyuma.com.glaiddriver.helpers.buttonClickAnim


class GetStartedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)
        val w: Window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }

        val prefManager = PrefManager(applicationContext)
        if (prefManager.isFirstTimeLaunch()) {
            prefManager.setFirstTimeLaunch(false)
            startActivity(Intent(this, CarouselActivity::class.java))
            finish()
        }
    }
    fun emailSignUpBtnClick(view: View){
        view.startAnimation(buttonClickAnim)
        println("view $view")
        val signUpIntent = Intent(this, SignUpActivity::class.java)
        startActivity(signUpIntent)

    }
    fun fbSignUpBtnClick(view: View){
        view.startAnimation(buttonClickAnim)

    }
    fun googleSignUpBtnClick(view: View){
        view.startAnimation(buttonClickAnim)

    }
    fun getStartedSignInBtnClick(view: View){
        view.startAnimation(buttonClickAnim)
        val signInIntent = Intent(this, LoginActivity::class.java)
        startActivity(signInIntent)

    }
    fun termOfUseClick(view: View) {
        view.startAnimation(buttonClickAnim)}
}

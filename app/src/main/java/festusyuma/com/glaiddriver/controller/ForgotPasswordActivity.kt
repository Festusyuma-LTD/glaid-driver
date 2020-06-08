package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.utilities.EXTRA_RECOVERY_TYPE
import festusyuma.com.glaiddriver.utilities.buttonClickAnim
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var recoveryType : String
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
        setContentView(R.layout.activity_forgot_password)
    }

    fun resetPasswordBtnClick(view: View) {
        view.startAnimation(buttonClickAnim)
        if( recoveryType !== ""){
            var finishedPage = Intent(this, RecoveryActivity::class.java)
//            PROPS
            finishedPage.putExtra(EXTRA_RECOVERY_TYPE,recoveryType)
            startActivity(finishedPage)
            //reset values on next page
//            recoveryType =""
            iKnowPhoneNumber.isChecked = false
            iKnowEmail.isChecked = false
        }else{
            Toast.makeText(this, "Please select a Recovery Method!", Toast.LENGTH_SHORT).show()
        }

    }

    fun iKnowEmailClick(view: View) {
        view.startAnimation(buttonClickAnim)
        recoveryType = "email"
        iKnowPhoneNumber.isChecked = false
    }

    fun iKnowPhoneNumberClick(view: View) {
        view.startAnimation(buttonClickAnim)
        recoveryType = "phone"
        iKnowEmail.isChecked = false
    }


}

package festusyuma.com.glaiddriver.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.utilities.EXTRA_RECOVERY_TYPE
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var recoveryType : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
    }

    fun resetPasswordBtnClick(view: View) {
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
        recoveryType = "email"
        iKnowPhoneNumber.isChecked = false
    }

    fun iKnowPhoneNumberClick(view: View) {
        recoveryType = "phone"
        iKnowEmail.isChecked = false
    }


}

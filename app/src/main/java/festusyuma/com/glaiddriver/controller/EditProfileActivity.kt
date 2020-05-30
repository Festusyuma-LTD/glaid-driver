package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import festusyuma.com.glaiddriver.R

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val w: Window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

            )
        }
        w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        w.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
    }

    fun changePasswordClick(view: View) {}
    fun signOutClick(view: View) {}
    fun connectFbClick(view: View) {}
    fun editBackBtnClick(view: View) {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}

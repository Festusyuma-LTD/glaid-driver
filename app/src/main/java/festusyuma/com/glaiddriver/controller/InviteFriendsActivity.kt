package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.helpers.buttonClickAnim

class InviteFriendsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_friends)
    }

    fun twitterShareBtnClick(view: View) {
        view.startAnimation(buttonClickAnim)
    }

    fun whatsappShareBtnClick(view: View) {
        view.startAnimation(buttonClickAnim)
    }

    fun fbShareBtnClick(view: View) {
        view.startAnimation(buttonClickAnim)
    }

    fun sendEmailBtnClick(view: View) {
        view.startAnimation(buttonClickAnim)
    }

    fun helpBackClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    fun copyShareCode(view: View) {
        view.startAnimation(buttonClickAnim)
    }
}

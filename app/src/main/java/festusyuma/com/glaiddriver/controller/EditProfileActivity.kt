package festusyuma.com.glaiddriver.controller

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.helpers.auth
import festusyuma.com.glaiddriver.helpers.buttonClickAnim
import festusyuma.com.glaiddriver.helpers.gson
import festusyuma.com.glaiddriver.models.User
import kotlinx.android.synthetic.main.activity_login.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var userDetails: User
    private lateinit var authPref: SharedPreferences
    private lateinit var dataPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        authPref = getSharedPreferences(getString(R.string.auth_key_name), Context.MODE_PRIVATE)
        dataPref = getSharedPreferences(getString(R.string.cached_data), Context.MODE_PRIVATE)

        if (dataPref.contains(getString(R.string.sh_user_details))) {

            val user = dataPref.getString(getString(R.string.sh_user_details), null)
            if (user != null) {
                userDetails = gson.fromJson(user, User::class.java)
                populateDetails()
                Log.v("ApiLog", "Response lass: $user")
            }
        }else logout()
    }

    private fun populateDetails() {
        val fullNameTV: TextView = findViewById(R.id.fullNameInput)
        val emailTV: TextView = findViewById(R.id.emailInput)
        val telTV: TextView = findViewById(R.id.telInput)


        fullNameTV.text = userDetails.fullName
        emailTV.text = userDetails.email
        telTV.text = userDetails.tel
    }

    fun changePasswordClick(view: View) {
        view.startAnimation(buttonClickAnim)
    }

    fun logout(view: View? = null) {
        view?.startAnimation(buttonClickAnim)

        with(authPref.edit()) {
            clear()
            commit()
        }

        with(dataPref.edit()) {
            clear()
            commit()
        }

        auth.signOut().let {
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }

    fun connectFbClick(view: View) {
        view.startAnimation(buttonClickAnim)
    }

    fun editBackBtnClick(view: View) {
        finish()
    }
}

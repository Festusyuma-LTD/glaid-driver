package festusyuma.com.glaiddriver.controller

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.helpers.buttonClickAnim
import festusyuma.com.glaiddriver.helpers.gson
import festusyuma.com.glaiddriver.models.User
import festusyuma.com.glaiddriver.request.Authentication
import festusyuma.com.glaiddriver.request.UserRequests
import java.lang.Exception

class EditProfileActivity : AppCompatActivity() {

    private lateinit var userDetails: User
    private lateinit var dataPref: SharedPreferences
    private lateinit var userRequests: UserRequests
    private lateinit var profileImage: ImageView
    private val imageUploadCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        dataPref = getSharedPreferences(getString(R.string.cached_data), Context.MODE_PRIVATE)
        userRequests = UserRequests(this)

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
        profileImage = findViewById(R.id.profileImage)
        profileImage.setOnClickListener{ updateProfileImage() }

        fullNameTV.text = userDetails.fullName
        emailTV.text = userDetails.email
        telTV.text = userDetails.tel
        userDetails.profileImage?.let { setProfileImage(it) }
    }

    fun changePasswordClick(view: View) {
        view.startAnimation(buttonClickAnim)
    }

    fun logout(view: View? = null) {
        view?.startAnimation(buttonClickAnim)
        Authentication(this).logout()
    }

    fun connectFbClick(view: View) {
        view.startAnimation(buttonClickAnim)
    }

    fun editBackBtnClick(view: View) {
        finish()
    }

    private fun updateProfileImage() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, imageUploadCode)
    }

    private fun setProfileImage(url: String) {
        Picasso
            .get()
            .load(url)
            .placeholder(R.drawable.pic)
            .error(R.drawable.pic)
            .fit()
            .into(profileImage, object: Callback {
                override fun onSuccess() { userRequests.setLoading(false) }
                override fun onError(e: Exception?) { userRequests.showError("Error displaying picture") }
            })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            imageUploadCode -> {
                if (data != null) {
                    val imageUri = data.data?: return
                    userRequests.uploadImage(imageUri) {
                        setProfileImage(it)
                    }
                }
            }
            else -> return
        }
    }
}

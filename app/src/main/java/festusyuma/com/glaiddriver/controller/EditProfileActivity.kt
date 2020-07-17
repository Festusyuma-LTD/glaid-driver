package festusyuma.com.glaiddriver.controller

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.models.User
import festusyuma.com.glaiddriver.utilities.buttonClickAnim
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private var profilePicUri: Uri? = null
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
        setContentView(R.layout.activity_edit_profile)
        //get new photo selected
        upload_image_edit.setOnClickListener {
            Log.d("EditProfileActivity", "EditProfileActivity clicked")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("EditProfileActivity", "EditProfileActivity onActivity Result")
            profilePicUri = data.data
//            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                ImageDecoder.createSource(contentResolver,uri!!)
//            } else {
//            }
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, profilePicUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            edit_upload_img.setImageDrawable(bitmapDrawable)
        }
    }

    private fun uploadImageToFireStore() {
        if (profilePicUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(profilePicUri!!)
            .addOnCompleteListener {
                Log.d("EditProfileActivity", "image uploaded: ${it.result?.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("EditProfileActivity", "image uploaded: $uri")
//                    saveUserToFirebaseDatabase(uri.toString())
                }

            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
    }

//    private fun saveUserToFirebaseDatabase(photoUrlPath :String) {
//        val uid = FirebaseAuth.getInstance().uid ?: "henry"
//        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//        val user = User(uid,fullName.text.toString(),photoUrlPath)
//        ref.setValue(user)
//            .addOnSuccessListener {
//                Log.d("EditProfileActivity","successfulled added to database")
//            }
//            .addOnFailureListener {
//                Log.d("EditProfileActivity","failed added to database ${it.message}")
//            }
//    }

    fun changePasswordClick(view: View) {
        view.startAnimation(buttonClickAnim)
        //
        uploadImageToFireStore()
    }

    fun signOutClick(view: View) {
        view.startAnimation(buttonClickAnim)
    }

    fun connectFbClick(view: View) {
        view.startAnimation(buttonClickAnim)
    }

    fun editBackBtnClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}

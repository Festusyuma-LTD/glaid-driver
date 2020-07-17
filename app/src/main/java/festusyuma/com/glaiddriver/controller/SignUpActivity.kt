package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.models.UserSignUp
import festusyuma.com.glaiddriver.utilities.buttonClickAnim
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    lateinit var fullName: String
    lateinit var emailField: String
    lateinit var phoneNumber: String
    lateinit var passwordField: String
    lateinit var repasswordField: String

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }

    fun toggleRePasswordClick(view: View) {
        view.startAnimation(buttonClickAnim)

    }

    fun togglePasswordClick(view: View) {
        view.startAnimation(buttonClickAnim)

    }

    fun submitSignUpBtnClick(view: View) {
        view.startAnimation(buttonClickAnim)

        createFirebaseAuth()
//        val intent = Intent(this, SignupOtpActivity::class.java)
//        startActivity(intent)
    }

    private fun createFirebaseAuth() {
        fullName = sign_up_full_name_iput.text.toString()
        emailField = sign_up_email_input.text.toString()
        phoneNumber = sign_up_phone_input.text.toString()
        passwordField = sign_up_password_input.text.toString()
        repasswordField = sign_up_reenter_password_input.text.toString()

        if (passwordField != repasswordField) {
            Toast.makeText(this, "Passwords unidentical", Toast.LENGTH_LONG).show()
            return
        }
        val firebaseauth: FirebaseAuth = FirebaseAuth.getInstance();
        //code to create new user
        firebaseauth.createUserWithEmailAndPassword(emailField, passwordField)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                //else if suscessful
                Log.d("LoginActivity,", "sucesffully created user ${it.result?.user?.uid}")
                saveUserToFirebaseDatabase()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                Log.d("LoginActivity,", "failed created user ${it.message}")
            }

        println(firebaseauth)
    }

    private fun saveUserToFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = UserSignUp(fullName, emailField, phoneNumber)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("EditProfileActivity", "successfulled added to database")
                //sign in user
                signinFirebaseAuth()
            }
            .addOnFailureListener {
                Log.d("EditProfileActivity", "failed added to database ${it.message}")
            }
    }

    private fun signinFirebaseAuth() {
        val firebaseauth: FirebaseAuth = FirebaseAuth.getInstance();
        //code to create new user
        firebaseauth.signInWithEmailAndPassword(emailField, passwordField)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                //else if suscessful
                Log.d("LoginActivity,", "sucesffully signin user ${it.result?.user?.uid}")

                val signUpIntent = Intent(this, MapsActivity::class.java)
                //code to clear previous activities
                signUpIntent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(signUpIntent)
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                Log.d("LoginActivity,", "failed signin user ${it.message}")
            }

        println(firebaseauth)
    }

}

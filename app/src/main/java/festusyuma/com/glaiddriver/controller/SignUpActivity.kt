package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.wang.avi.AVLoadingIndicatorView
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.helpers.Api
import festusyuma.com.glaiddriver.helpers.buttonClickAnim
import festusyuma.com.glaiddriver.helpers.gson
import festusyuma.com.glaiddriver.request.UserRegistrationRequest
import org.json.JSONObject
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private var operationRunning = false
    private lateinit var loadingCover: ConstraintLayout
    private lateinit var loadingAvi: AVLoadingIndicatorView
    private lateinit var errorMsg: TextView
    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        queue = Volley.newRequestQueue(this)
        initLoaders()
    }

    fun initLoaders() {
        loadingCover = findViewById(R.id.loadingCoverConstraint)
        loadingAvi = loadingCover.findViewById(R.id.avi)
        errorMsg = findViewById(R.id.errorMsg)
    }

    fun toggleRePasswordClick(view: View) {
        view.startAnimation(buttonClickAnim)

    }

    fun togglePasswordClick(view: View) {
        view.startAnimation(buttonClickAnim)
    }

    fun signUp(view: View) {
        if (!operationRunning) {
            setLoading(true)
            val userRequest = getUserRequest()

            if (!hasError(userRequest)) {
                val queue = Volley.newRequestQueue(this)
                val userJsonObject = JSONObject(gson.toJson(userRequest))

                val request = JsonObjectRequest(
                    Request.Method.POST,
                    Api.REGISTER,
                    userJsonObject,
                    Response.Listener {
                            response ->
                        if (response.getInt("status") == 200) {
                            val signUpIntent = Intent(this, SignupOtpActivity::class.java)
                            signUpIntent.putExtra("userRequest", userRequest)

                            startActivity(signUpIntent)
                        }else showError(response.getString("message"))

                        setLoading(false)
                    },
                    Response.ErrorListener {
                            response ->
                        if (response.networkResponse != null) {
                            showError(getString(R.string.error_occurred))
                            response.printStackTrace()
                        }else showError(getString(R.string.internet_error_msg))

                        setLoading(false)
                    }
                )

                queue.add(request)
            }else setLoading(false)
        }
    }

    private fun getUserRequest(): UserRegistrationRequest {
        return UserRegistrationRequest(
            findViewById<EditText>(R.id.fullNameInput).text.toString(),
            findViewById<EditText>(R.id.emailInput).text.toString(),
            findViewById<EditText>(R.id.telInput).text.toString(),
            findViewById<EditText>(R.id.passwordInput).text.toString()
        )
    }

    private fun hasError(userRequest: UserRegistrationRequest): Boolean {
        var error = false
        val verifyPassword = findViewById<EditText>(R.id.verifyPasswordInput)
        val fullNameError = findViewById<TextView>(R.id.fullNameInputError)
        val emailError = findViewById<TextView>(R.id.emailInputError)
        val telError = findViewById<TextView>(R.id.telInputError)
        val passwordError = findViewById<TextView>(R.id.passwordInputError)
        val verifyPasswordError = findViewById<TextView>(R.id.verifyPasswordInputError)
        val emailRegex = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])"

        when {
            userRequest.fullName == "" -> {
                fullNameError.setText(R.string.field_empty)
                error = true
            }
            !userRequest.fullName.matches(Regex("^[a-zA-Z ]*$")) -> {
                fullNameError.setText(R.string.invalid_format)
                error = true
            }
            else -> fullNameError.text = ""
        }

        when {
            userRequest.email == "" -> {
                emailError.setText(R.string.field_empty)
                error = true
            }
            !userRequest.email.toLowerCase(Locale.ROOT).matches(Regex(emailRegex)) -> {
                emailError.setText(R.string.invalid_format)
                error = true
            }
            else -> emailError.text = ""
        }

        when {
            userRequest.tel == "" -> {
                telError.setText(R.string.field_empty)
                error = true
            }
            !userRequest.tel.matches(Regex("^[0-9]*$")) -> {
                telError.setText(R.string.invalid_format)
                error = true
            }else -> telError.text = ""
        }

        when {
            userRequest.password == "" -> {
                passwordError.setText(R.string.field_empty)
                error = true
            }
            !userRequest.password.matches(Regex("^(?=.*[0-9])(?=.*[a-z]).{6,}")) -> {
                passwordError.setText(R.string.password_invalid)
                error = true
            }
            else -> passwordError.text = ""
        }

        if (userRequest.password != verifyPassword.text.toString()) {
            verifyPasswordError.setText(R.string.password_does_not_match)
            error = true
        }else verifyPasswordError.text = ""

        return error
    }

    private fun setLoading(loading: Boolean) {
        if (loading) {
            loadingCover.visibility = View.VISIBLE
            loadingAvi.show()
            operationRunning = true
        }else {
            loadingCover.visibility = View.GONE
            loadingAvi.hide()
            operationRunning = false
        }
    }

    private fun showError(msg: String) {
        errorMsg.text = msg
        errorMsg.visibility = View.VISIBLE
    }

    fun hideError(view: View) {
        errorMsg.visibility = View.INVISIBLE
    }
}

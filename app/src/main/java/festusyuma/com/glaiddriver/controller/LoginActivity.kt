package festusyuma.com.glaiddriver.controller

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.wang.avi.AVLoadingIndicatorView
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.helpers.*
import festusyuma.com.glaiddriver.request.LoginRequest
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private var operationRunning = false
    private lateinit var loadingCover: ConstraintLayout
    private lateinit var loadingAvi: AVLoadingIndicatorView
    private lateinit var errorMsg: TextView

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText

    private val TAG = "PermissionDemo"
    private val RECORD_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loadingCover = findViewById(R.id.loadingCoverConstraint)
        loadingAvi = loadingCover.findViewById(R.id.avi)
        errorMsg = findViewById(R.id.errorMsg)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        passwordEye.setOnClickListener {
            it.startAnimation(buttonClickAnim)
            val passwordField = findViewById<EditText>(R.id.passwordInput)
            if (passwordEye.drawable.constantState == resources.getDrawable(
                    R.drawable.ic_password_eye,
                    theme
                ).constantState
            ) {
                passwordEye.setImageResource(R.drawable.ic_password_eye_closed)
                passwordField.inputType =
                    InputType.TYPE_CLASS_TEXT

            } else if (passwordEye.drawable.constantState == resources.getDrawable(
                    R.drawable.ic_password_eye_closed,
                    theme
                ).constantState
            ) {
                passwordEye.setImageResource(R.drawable.ic_password_eye)
                passwordField.inputType =
                    InputType.TYPE_TEXT_VARIATION_PASSWORD

            }

        }
    }

    fun loginBtnClick(view: View) {
        if (!operationRunning) {
            setLoading(true)

            val loginRequest = LoginRequest(
                emailInput.text.toString(),
                passwordInput.text.toString()
            )

            val queue = Volley.newRequestQueue(this)
            val loginRequestJson = JSONObject(gson.toJson(loginRequest))

            val request = JsonObjectRequest(
                Request.Method.POST,
                Api.LOGIN,
                loginRequestJson,
                Response.Listener { response ->
                    if (response.getInt("status") == 200) {
                        val authPref = getSharedPreferences(
                            getString(R.string.auth_key_name),
                            Context.MODE_PRIVATE
                        )
                        val data = response.getJSONObject("data")
                        val serverToken = data.getString("token")

                        auth.signInWithCustomToken(serverToken)
                            .addOnSuccessListener { res ->
                                val user = res.user

                                user?.getIdToken(true)
                                    ?.addOnSuccessListener { tokenRes ->
                                        val token = tokenRes.token
                                        if (token != null) {
                                            with(authPref.edit()) {
                                                putString(getString(R.string.sh_token), token)
                                                commit()
                                            }

                                            queue.add(dashboard(token))
                                        } else errorOccurred()
                                    }
                                    ?.addOnFailureListener { errorOccurred() }
                            }.addOnFailureListener { errorOccurred() }

                    } else {
                        errorOccurred(response.getString("message"))
                    }
                },
                Response.ErrorListener { response ->
                    if (response.networkResponse != null) {
                        showError(getString(R.string.error_occurred))
                        response.printStackTrace()
                    } else showError(getString(R.string.internet_error_msg))

                    setLoading(false)
                }
            )

            queue.add(request)
        }

        view.startAnimation(buttonClickAnim)
    }

    private fun errorOccurred(message: String? = null) {
        setLoading(false)
        showError(message ?: "An error occurred")
    }

    private fun dashboard(token: String): JsonObjectRequest {

        return object : JsonObjectRequest(
            Method.GET,
            Api.DASHBOARD,
            null,
            Response.Listener { response ->
                Dashboard.store(this, response.getJSONObject("data"))
                startActivity(Intent(this, MapsActivity::class.java))
                finishAffinity()
            },
            Response.ErrorListener { response ->
                if (response.networkResponse != null) {
                    if (response.networkResponse.statusCode == 403) {
                        showError("Not registered as driver")
                    } else showError("An error occurred")
                } else showError(getString(R.string.internet_error_msg))

                setLoading(false)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf(
                    "Authorization" to "Bearer $token"
                )
            }
        }
    }

    fun forgotPasswordClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val signUpIntent = Intent(this, ProblemsLoginActivity::class.java)
        startActivity(signUpIntent)

    }    // make permission request// shows user a dialog screen

    private fun setLoading(loading: Boolean) {
        if (loading) {
            loadingCover.visibility = View.VISIBLE
            operationRunning = true
        } else {
            loadingCover.visibility = View.GONE
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

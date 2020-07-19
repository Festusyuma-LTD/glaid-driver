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
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.wang.avi.AVLoadingIndicatorView
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.helpers.Api
import festusyuma.com.glaiddriver.helpers.buttonClickAnim
import festusyuma.com.glaiddriver.helpers.gson
import festusyuma.com.glaiddriver.request.PasswordResetRequest
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {

    private var operationRunning = false
    private lateinit var passwordResetRequest: PasswordResetRequest

    private lateinit var passwordInput: EditText
    private lateinit var passwordInputError: TextView
    private lateinit var verifyPasswordInput: EditText
    private lateinit var verifyPasswordInputError: TextView

    private lateinit var loadingCover: ConstraintLayout
    private lateinit var loadingAvi: AVLoadingIndicatorView
    private lateinit var errorMsg: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        loadingCover = findViewById(R.id.loadingCoverConstraint)
        loadingAvi = loadingCover.findViewById(R.id.avi)
        errorMsg = findViewById(R.id.errorMsg)

        passwordInput = findViewById(R.id.passwordInput)
        passwordInputError = findViewById(R.id.passwordInputError)
        verifyPasswordInput = findViewById(R.id.verifyPasswordInput)
        verifyPasswordInputError = findViewById(R.id.verifyPasswordInputError)

        passwordResetRequest = intent.getSerializableExtra("resetRequest") as PasswordResetRequest
    }

    fun resetBtnClick(view: View) {
        if (!operationRunning) {
            setLoading(true)
            view.startAnimation(buttonClickAnim)
            passwordResetRequest.newPassword = passwordInput.text.toString()

            if (!hasError()) {
                val queue = Volley.newRequestQueue(this)
                val resetJsonObject = JSONObject(gson.toJson(passwordResetRequest))

                val request = JsonObjectRequest(
                    Request.Method.POST,
                    Api.RESET_PASSWORD,
                    resetJsonObject,
                    Response.Listener { response ->
                        if (response.getInt("status") == 200) {
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        }else showError(response.getString("message"))

                        setLoading(false)
                    },
                    Response.ErrorListener { response ->
                        response.printStackTrace()
                        showError("An error occurred")
                        setLoading(false)
                    }
                )

                queue.add(request)
            }else setLoading(false)
        }
    }

    private fun hasError(): Boolean {
        var error = false

        when {
            passwordResetRequest.newPassword == null -> {
                passwordInputError.setText(R.string.field_empty)
                error = true
            }
            passwordResetRequest.newPassword == "" -> {
                passwordInputError.setText(R.string.field_empty)
                error = true
            }
            !passwordResetRequest.newPassword?.matches(Regex("^(?=.*[0-9])(?=.*[a-z]).{6,}"))!! -> {
                passwordInputError.setText(R.string.password_invalid)
                error = true
            }
            else -> passwordInputError.text = ""
        }

        if (passwordResetRequest.newPassword != verifyPasswordInput.text.toString()) {
            verifyPasswordInputError.setText(R.string.password_does_not_match)
            error = true
        }else verifyPasswordInputError.text = ""

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

package festusyuma.com.glaiddriver.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.wang.avi.AVLoadingIndicatorView
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.helpers.Api
import festusyuma.com.glaiddriver.helpers.gson
import festusyuma.com.glaiddriver.requestdto.PasswordResetRequest
import org.json.JSONObject

class ForgotPasswordFinalOtpActivity : AppCompatActivity() {

    private lateinit var passwordResetRequest: PasswordResetRequest
    private lateinit var otpResetIntroText: TextView

    private var operationRunning = false
    private lateinit var loadingCover: ConstraintLayout
    private lateinit var loadingAvi: AVLoadingIndicatorView
    private lateinit var errorMsg: TextView
    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_final_otp)

        queue = Volley.newRequestQueue(this)
        otpResetIntroText = findViewById(R.id.resetPasswordIntroText)
        passwordResetRequest = intent.getSerializableExtra("resetRequest") as PasswordResetRequest
        val inputLabel = if (passwordResetRequest.email != null) "Email" else "Phone number"
        otpResetIntroText.text = getString(R.string.otp_reset_intro_text).format(inputLabel)

        loadingCover = findViewById(R.id.loadingCoverConstraint)
        loadingAvi = loadingCover.findViewById(R.id.avi)
        errorMsg = findViewById(R.id.errorMsg)
    }

    fun resendOtpClick(view: View) {
        if (!operationRunning) {
            setLoading(true)

            passwordResetRequest.otp = null
            passwordResetRequest(Api.RESET_PASSWORD) {response->
                if (response.getInt("status") == 200) {
                    Toast.makeText(this, "Otp Reset", Toast.LENGTH_SHORT).show()
                }else showError(response.getString("message"))

                setLoading(false)
            }
        }
    }

    fun resetPasswordMethod(view: View){
        if (!operationRunning) {
            setLoading(true)

            val otpInput: EditText = findViewById(R.id.otpInput)
            passwordResetRequest.otp = otpInput.text.toString()
            passwordResetRequest(Api.VALIDATE_OTP) {response ->
                if (response.getInt("status") == 200) {
                    val intent = Intent(this, ResetPasswordActivity::class.java);
                    intent.putExtra("resetRequest", passwordResetRequest)

                    startActivity(intent)
                }else showError(response.getString("message"))

                setLoading(false)
            }
        }
    }

    private fun passwordResetRequest(url: String, listener: (response: JSONObject) -> Unit) {
        val resetJsonObject = JSONObject(gson.toJson(passwordResetRequest))

        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            resetJsonObject,
            Response.Listener {
                listener(it)
            },
            Response.ErrorListener { response ->
                response.printStackTrace()
                showError("An error occurred")
                setLoading(false)
            }
        )

        queue.add(request)
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

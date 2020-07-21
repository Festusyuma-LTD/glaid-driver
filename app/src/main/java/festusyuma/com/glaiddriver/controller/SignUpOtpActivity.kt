package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
import festusyuma.com.glaiddriver.requestdto.UserRegistrationRequest
import org.json.JSONObject

class SignUpOtpActivity : AppCompatActivity() {

    private lateinit var userRequest: UserRegistrationRequest

    private var operationRunning: Boolean = false
    private lateinit var loadingCover: ConstraintLayout
    private lateinit var loadingAvi: AVLoadingIndicatorView
    private lateinit var errorMsg: TextView

    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_otp)

        queue = Volley.newRequestQueue(this)
        userRequest = intent.getSerializableExtra("userRequest") as UserRegistrationRequest
        initLoaders()
    }

    private fun initLoaders() {
        loadingCover = findViewById(R.id.loadingCoverConstraint)
        loadingAvi = loadingCover.findViewById(R.id.avi)
        errorMsg = findViewById(R.id.errorMsg)
    }

    fun completeSignUp(view: View) {
        if (!operationRunning) {
            setLoading(true)
            view.startAnimation(buttonClickAnim)

            val otpInput = findViewById<EditText>(R.id.otpInput)
            userRequest.otp = otpInput.text.toString()

            sendSignUpRequest {response ->
                if (response.getInt("status") == 200) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }else showError(response.getString("message"))

                setLoading(false)
            }
        }
    }

    fun resendOtpClick(view: View) {
        if (!operationRunning) {
            setLoading(true)
            view.startAnimation(buttonClickAnim)

            userRequest.otp = null
            sendSignUpRequest {response ->
                if (response.getInt("status") == 200) {
                    Toast.makeText(this, "OTP Sent", Toast.LENGTH_SHORT).show()
                }else showError(response.getString("message"))

                setLoading(false)
            }
        }
    }

    private fun sendSignUpRequest(listener: (response: JSONObject) -> Unit) {
        val userJsonObject = JSONObject(gson.toJson(userRequest))

        val request = JsonObjectRequest(
            Request.Method.POST,
            Api.REGISTER,
            userJsonObject,
            Response.Listener {
                listener(it)
            },
            Response.ErrorListener { response ->
                if (response.networkResponse != null) {
                    showError(getString(R.string.error_occurred))
                    response.printStackTrace()
                }else showError(getString(R.string.internet_error_msg))
            }
        )

        request.tag = "sign_up"
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

    private fun showError(errorMsg: String) {
        this.errorMsg.text = errorMsg
        this.errorMsg.visibility = View.VISIBLE
    }

    fun hideError(view: View) {
        errorMsg.visibility = View.INVISIBLE
    }
}

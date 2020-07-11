package festusyuma.com.glaiddriver.controller

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.wang.avi.AVLoadingIndicatorView
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.helpers.Api
import festusyuma.com.glaiddriver.helpers.Dashboard
import festusyuma.com.glaiddriver.helpers.buttonClickAnim
import festusyuma.com.glaiddriver.helpers.gson
import festusyuma.com.glaiddriver.request.LoginRequest
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
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loadingCover = findViewById(R.id.loadingCoverConstraint)
        loadingAvi = loadingCover.findViewById(R.id.avi)
        errorMsg = findViewById(R.id.errorMsg)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
    }

    fun togglePasswordClick(view: View) {
        view.startAnimation(buttonClickAnim)

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
                Response.Listener {
                        response ->
                    if (response.getInt("status") == 200) {
                        val sharedPref = getSharedPreferences("auth_token", Context.MODE_PRIVATE)
                        val data = response.getJSONObject("data")
                        val token = data.getString("token")

                        with (sharedPref.edit()) {
                            putString(getString(R.string.auth_key_name), token)
                            commit()
                        }

                        queue.add(dashboard(token))
                    }else {
                        setLoading(false)
                        showError(response.getString("message"))
                    }
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
        }

        view.startAnimation(buttonClickAnim)
    }

    private fun dashboard(token: String): JsonObjectRequest {

        return object : JsonObjectRequest(
            Method.GET,
            Api.DASHBOARD,
            null,
            Response.Listener {
                    response ->
                Dashboard.store(this, response.getJSONObject("data"))
                startActivity(Intent(this, MapsActivity::class.java))
                finishAffinity()
            },
            Response.ErrorListener { response->
                if (response.networkResponse != null) {
                    if (response.networkResponse.statusCode == 403) {
                        showError("Not registered as driver")
                    }else showError("An error occurred")
                }else showError(getString(R.string.internet_error_msg))

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

    }    // make permission request// shows user a dialog scrren

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            RECORD_REQUEST_CODE
        )
    }

    //function to check user permission//call in the context of needed permission
    private fun setupPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            makeRequest()
        } else {
            makeRequest()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //here you can either end the app or change intent

                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Permission to access the device location is required for this app to access your location.")
                        .setTitle("Permission required")
                    builder.setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        Log.i(TAG, "Clicked")
                        makeRequest()
                    }
                    builder.setNegativeButton(
                        "CANCEL"
                    ) { _, _ ->
                        Log.i(TAG, "Clicked")
                        val myToast = Toast.makeText(
                            applicationContext,
                            "${resources.getString(R.string.app_name)} requires location to continue",
                            Toast.LENGTH_SHORT
                        )
                        myToast.setGravity(Gravity.BOTTOM or Gravity.CENTER_VERTICAL, 0, -50)
                        myToast.show()
                    }
                    val dialog = builder.create()
                    dialog.show()
                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        if (loading) {
            loadingCover.visibility = View.VISIBLE
            operationRunning = true
        }else {
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

    fun logout() {
        val sharedPref = getSharedPreferences("auth_token", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove(getString(R.string.auth_key_name))
            commit()
        }

        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }
}

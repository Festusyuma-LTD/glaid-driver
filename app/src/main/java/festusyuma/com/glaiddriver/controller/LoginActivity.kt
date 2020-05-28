package festusyuma.com.glaiddriver.controller

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import festusyuma.com.glaiddriver.R

class LoginActivity : AppCompatActivity() {
    private val TAG = "PermissionDemo"
    private val RECORD_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val w: Window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        w.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
    }

    fun togglePasswordClick(view: View) {

    }

    fun loginBtnClick(view: View) {
        val locationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            setupPermissions()
            return
        }
        val signUpIntent = Intent(this, MapsActivity::class.java)
        startActivity(signUpIntent)

    }

    fun forgotPasswordClick(view: View) {
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
}

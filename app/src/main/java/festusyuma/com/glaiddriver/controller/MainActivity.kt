package festusyuma.com.glaiddriver.controller

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.helpers.Api
import festusyuma.com.glaiddriver.helpers.Dashboard
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    // This is the loading time of the splash screen
    private val splashDelayTimeZone: Long = 1000 // 1 sec
    private lateinit var queue: RequestQueue


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        val authPref = getSharedPreferences(getString(R.string.auth_key_name), Context.MODE_PRIVATE)
        if (authPref.contains(getString(R.string.sh_token))) {

            val auth = authPref.getString(getString(R.string.sh_token), "")
            if (auth != null) {

                queue = Volley.newRequestQueue(this)
                val req = dashboard(auth) { response ->
                    if (response.getInt("status") == 200) {
                        startMapsActivity(response.getJSONObject("data"))
                    } else startCarouselActivity()
                }

                queue.add(req)
            }else startCarouselActivity()
        }else startCarouselActivity()
    }

    private fun startMapsActivity(data: JSONObject) {
        Dashboard.store(this, data)
        startActivity(Intent(this, MapsActivity::class.java))
        finishAffinity()
    }

    private fun startCarouselActivity() {
        Handler().postDelayed({
            startActivity(Intent(this, CarouselActivity::class.java))
            finishAffinity()
        }, splashDelayTimeZone)
    }

    private fun dashboard(token: String, listener: (response: JSONObject) -> Unit): JsonObjectRequest {

        return object : JsonObjectRequest(
            Method.GET,
            Api.DASHBOARD,
            null,
            Response.Listener {
                listener(it)
            },
            Response.ErrorListener { response->
                if (response.networkResponse == null) {
                    startActivity(Intent(this, MapsActivity::class.java))
                    finishAffinity()
                }else {
                    Log.v("ApiLog", response.networkResponse.statusCode.toString())
                    logout()
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf(
                    "Authorization" to "Bearer $token"
                )
            }
        }
    }

    fun logout() {
        val dataPref = getSharedPreferences(getString(R.string.auth_key_name), Context.MODE_PRIVATE)
        val authPref = getSharedPreferences(getString(R.string.cached_data), Context.MODE_PRIVATE)

        with(authPref.edit()) {
            clear()
            commit()
        }

        with(dataPref.edit()) {
            clear()
            commit()
        }

        startActivity(Intent(this, GetStartedActivity::class.java))
        finishAffinity()
    }
}

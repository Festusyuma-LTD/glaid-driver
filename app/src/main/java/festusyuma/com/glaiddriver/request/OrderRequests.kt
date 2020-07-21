package festusyuma.com.glaiddriver.request

import android.app.Activity
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import festusyuma.com.glaiddriver.helpers.API_LOG_TAG
import festusyuma.com.glaiddriver.helpers.Api

class OrderRequests(private val c: Activity): Authentication(c) {

    private val queue = Volley.newRequestQueue(c)

    fun startTrip(callback: () -> Unit) {
        getAuthentication { authorization ->
            val req = object : JsonObjectRequest(
                Method.GET,
                Api.DASHBOARD,
                null,
                Response.Listener { response ->
                    Log.v(API_LOG_TAG, "$response")
                },

                Response.ErrorListener { response->
                    if (response.networkResponse != null) {
                        if (response.networkResponse.statusCode == 403) {
                            Log.v(API_LOG_TAG, "")
                        }else Log.v(API_LOG_TAG, "")
                    }else Log.v(API_LOG_TAG, "")

                    setLoading(false)
                }
            ){
                override fun getHeaders(): MutableMap<String, String> {
                    return authorization
                }
            }
        }
    }
}
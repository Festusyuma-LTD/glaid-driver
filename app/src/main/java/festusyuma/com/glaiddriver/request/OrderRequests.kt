package festusyuma.com.glaiddriver.request

import android.app.Activity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import festusyuma.com.glaiddriver.helpers.Api
import festusyuma.com.glaiddriver.helpers.CHECK_YOUR_INTERNET
import festusyuma.com.glaiddriver.helpers.ERROR_OCCURRED_MSG
import org.json.JSONObject

class OrderRequests(private val c: Activity): Authentication(c) {

    private val queue = Volley.newRequestQueue(c)

    fun startTrip(callback: (response: JSONObject) -> Unit) {
        getAuthentication { authorization ->
            val req = object : JsonObjectRequest(
                Method.GET,
                Api.START_TRIP,
                null,
                Response.Listener { response ->
                    if (response.getInt("status") == 200) {
                        callback(response)
                    }else showError(response.getString("message"))
                },

                Response.ErrorListener { response->
                    if (response.networkResponse != null) {
                        if (response.networkResponse.statusCode == 403) {
                            logout()
                        }else showError(ERROR_OCCURRED_MSG)
                    }else showError(CHECK_YOUR_INTERNET)

                    setLoading(false)
                }
            ){
                override fun getHeaders(): MutableMap<String, String> {
                    return authorization
                }
            }
        }
    }

    fun completeTrip(callback: (response: JSONObject) -> Unit) {
        getAuthentication { authorization ->
            val req = object : JsonObjectRequest(
                Method.GET,
                Api.COMPLETE_TRIP,
                null,
                Response.Listener { response ->
                    if (response.getInt("status") == 200) {
                        callback(response)
                    }else showError(response.getString("message"))
                },

                Response.ErrorListener { response->
                    if (response.networkResponse != null) {
                        if (response.networkResponse.statusCode == 403) {
                            logout()
                        }else showError(ERROR_OCCURRED_MSG)
                    }else showError(CHECK_YOUR_INTERNET)

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
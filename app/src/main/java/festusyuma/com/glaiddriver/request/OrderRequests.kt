package festusyuma.com.glaiddriver.request

import android.app.Activity
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import festusyuma.com.glaiddriver.helpers.*
import festusyuma.com.glaiddriver.requestdto.RatingRequest
import org.json.JSONObject

class OrderRequests(private val c: Activity): Authentication(c) {

    private val queue = Volley.newRequestQueue(c)

    fun getOrderDetails(orderId: Long, callback: (response: JSONObject) -> Unit) {
        getAuthentication { authorization ->
            val req = object : JsonObjectRequest(
                Method.GET,
                Api.orderDetails(orderId),
                null,
                Response.Listener { response ->
                    if (response.getInt("status") == 200) {
                        callback(response.getJSONObject("data"))
                    }else showError(response.getString("message"))

                    setLoading(false)
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

            req.retryPolicy = defaultRetryPolicy
            req.tag = "update_order_status"
            queue.add(req)
        }
    }

    fun startTrip(callback: (response: JSONObject) -> Unit) {
        if (!operationRunning) {
            setLoading(true)

            getAuthentication { authorization ->
                val req = object : JsonObjectRequest(
                    Method.GET,
                    Api.START_TRIP,
                    null,
                    Response.Listener { response ->
                        if (response.getInt("status") == 200) {
                            callback(response)
                        }else showError(response.getString("message"))

                        setLoading(false)
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

                req.retryPolicy = defaultRetryPolicy
                req.tag = "update_order_status"
                queue.add(req)
            }
        }
    }

    fun completeTrip(callback: (response: JSONObject) -> Unit) {
        if (!operationRunning) {
            setLoading(true)

            getAuthentication { authorization ->
                val req = object : JsonObjectRequest(
                    Method.GET,
                    Api.COMPLETE_TRIP,
                    null,
                    Response.Listener { response ->
                        if (response.getInt("status") == 200) {
                            callback(response)
                        }else showError(response.getString("message"))
                        setLoading(false)
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

                req.retryPolicy = defaultRetryPolicy
                req.tag = "update_order_status"
                queue.add(req)
            }
        }
    }

    fun rateCustomer(ratingRequest: RatingRequest, callback: () -> Unit) {
        if (!operationRunning) {
            setLoading(true)

            val ratingRequestJsonObj = JSONObject(gson.toJson(ratingRequest))
            getAuthentication { authorization ->
                val req = object : JsonObjectRequest(
                    Method.POST,
                    Api.RATE_CUSTOMER,
                    ratingRequestJsonObj,
                    Response.Listener { response ->
                        if (response.getInt("status") == 200) {
                            callback()
                        }else showError(response.getString("message"))

                        setLoading(false)
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

                req.retryPolicy = defaultRetryPolicy
                req.tag = "rate_driver"
                queue.add(req)
            }
        }
    }

    fun confirmPayment(success: Boolean, callback: () -> Unit) {
        if (!operationRunning) {
            setLoading(true)

            getAuthentication { authorization ->
                val req = object : JsonObjectRequest(
                    Method.GET,
                    if (success) Api.CONFIRM_PAYMENT else Api.PAYMENT_FAILED,
                    null,
                    Response.Listener { response ->
                        if (response.getInt("status") == 200) {
                            callback()
                        }else showError(response.getString("message"))

                        setLoading(false)
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

                req.retryPolicy = defaultRetryPolicy
                req.tag = "rate_driver"
                queue.add(req)
            }
        }
    }
}
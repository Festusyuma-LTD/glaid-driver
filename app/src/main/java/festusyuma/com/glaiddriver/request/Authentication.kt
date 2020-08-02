package festusyuma.com.glaiddriver.request

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.controller.GetStartedActivity
import festusyuma.com.glaiddriver.helpers.auth

open class Authentication(private val c: Activity) : LoadingAndErrorHandler(c) {

    private lateinit var authPref: SharedPreferences

    fun getAuthentication(callback: (authentication: MutableMap<String, String>) -> Unit) {
        val authKeyName = c.getString(R.string.auth_key_name)
        val tokenKeyName = c.getString(R.string.sh_token)

        authPref = c.getSharedPreferences(authKeyName, Context.MODE_PRIVATE)
        if (authPref.contains(tokenKeyName)) {
            auth.currentUser?.getIdToken(true)
                ?.addOnSuccessListener {
                    val token = it.token
                    val serverToken = authPref.getString(tokenKeyName, null) ?: ""

                    if (token != null) callback(
                        mutableMapOf(
                            "Authorization" to "Bearer $token",
                            "ServerAuthorization" to serverToken
                        )
                    )
                }
                ?.addOnFailureListener { logout() }
        } else logout()
    }

    fun logout() {
        val dataPref =
            c.getSharedPreferences(c.getString(R.string.auth_key_name), Context.MODE_PRIVATE)
        val authPref =
            c.getSharedPreferences(c.getString(R.string.cached_data), Context.MODE_PRIVATE)

        with(authPref.edit()) {
            clear()
            commit()
        }

        with(dataPref.edit()) {
            clear()
            commit()
        }

        c.startActivity(Intent(c, GetStartedActivity::class.java))
        c.finishAffinity()
    }
}
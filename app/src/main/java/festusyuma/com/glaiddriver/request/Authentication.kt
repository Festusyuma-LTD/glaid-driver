package festusyuma.com.glaiddriver.request

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.helpers.auth

open class Authentication(private val c: Activity): LoadingAndErrorHandler(c) {

    private lateinit var authPref: SharedPreferences

    fun getAuthentication(callback: (authentication: MutableMap<String, String>) -> Unit) {
        val authKeyName = c.getString(R.string.auth_key_name)
        val tokenKeyName = c.getString(R.string.sh_token)

        authPref = c.getSharedPreferences(authKeyName, Context.MODE_PRIVATE)
        if (authPref.contains(tokenKeyName)) {
            auth.currentUser?.getIdToken(true)
                ?.addOnSuccessListener {
                    val token = it.token
                    val serverToken = authPref.getString(tokenKeyName, null)?: ""

                    if (token != null) callback(mutableMapOf(
                        "token" to token,
                        "serverToken" to serverToken
                    ))
                }
        }
    }
}
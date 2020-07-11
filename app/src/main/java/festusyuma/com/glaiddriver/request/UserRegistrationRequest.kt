package festusyuma.com.glaiddriver.request

import java.io.Serializable

data class UserRegistrationRequest (
    val fullName: String,
    val email: String,
    val tel: String,
    val password: String,
    var otp: String? = null
): Serializable
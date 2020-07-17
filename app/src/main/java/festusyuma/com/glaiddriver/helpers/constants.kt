package festusyuma.com.glaiddriver.helpers

import android.view.animation.AlphaAnimation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson


/**
 * Created by Chidozie Henry on Tuesday, May 19, 2020.
 * Email: okebugwuchidozie@gmail.com
 */

val fr = Firebase
val db = Firebase.firestore
val auth = FirebaseAuth.getInstance()
val gson = Gson()

const val EXTRA_RECOVERY_TYPE = "recoverType"
const val EXTRA_QUESTION = "question"
const val EXTRA_FORGOT_PASSWORD_CHOICE = "email"

val buttonClickAnim: AlphaAnimation? = AlphaAnimation(1f, 0.8f)

const val API_LOG_TAG = "apiLog"
const val APP_LOG_TAG = "appLog"
const val FIRE_STORE_LOG_TAG = "fireStoreLog"
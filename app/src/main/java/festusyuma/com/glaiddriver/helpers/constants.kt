package festusyuma.com.glaiddriver.helpers

import android.view.animation.AlphaAnimation
import com.google.gson.Gson


/**
 * Created by Chidozie Henry on Tuesday, May 19, 2020.
 * Email: okebugwuchidozie@gmail.com
 */
const val EXTRA_RECOVERY_TYPE = "recoverType"
const val EXTRA_QUESTION = "question"
const val EXTRA_FORGOT_PASSWORD_CHOICE = "email"

val gson = Gson()
val buttonClickAnim: AlphaAnimation? = AlphaAnimation(1f, 0.8f)
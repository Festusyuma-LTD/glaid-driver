package festusyuma.com.glaiddriver.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * Created by Chidozie Henry on Tuesday, July 14, 2020.
 * Email: okebugwuchidozie@gmail.com
 */
//@Parcelize
//class User(
//    val email: String,
//    val fullName: String,
//    val phone: String,
//    val photUrlPath: String,
//    val uid: String,
//    val username: String
//) : Parcelable {
//    constructor() : this("", "", "", "", "", "")
//}

@Parcelize
class UserSignUp(val fullName: String, val email: String, val phone: String) : Parcelable {
    constructor() : this("", "", "")
}

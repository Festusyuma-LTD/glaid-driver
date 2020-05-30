package festusyuma.com.glaiddriver.models

import android.graphics.Color


/**
 * Created by Chidozie Henry on Saturday, May 30, 2020.
 * Email: okebugwuchidozie@gmail.com
 */
class OrderHistory(
    var id: Int,
    var literValue: String,
    var oilType: String,
    var address: String,
    var deliverStatus: String
) {
    override fun toString(): String {
        return "$literValue $oilType : $deliverStatus"
    }

    fun deliveryStatusColor(): Int {
        return when (deliverStatus) {
            "Delivered" -> Color.parseColor("#4E007C")
            "Delivering..." -> Color.parseColor("#27AE60")
            "Delivery incomplete" -> Color.parseColor("#FC7400")
            else -> Color.parseColor("#FFFFFF")
        }
    }
}
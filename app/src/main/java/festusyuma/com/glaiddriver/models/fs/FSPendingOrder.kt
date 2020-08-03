package festusyuma.com.glaiddriver.models.fs

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import festusyuma.com.glaiddriver.helpers.OrderStatusCode

data class FSPendingOrder (
    var user: FSUser?= null,
    var quantity: Double?= null,
    var gasType: String?= null,
    var gasTypeUnit: String?= null,
    var amount: Double?= null,
    var driverId: Long? = null,
    var driver: FSUser? = null,
    var status: Long? = OrderStatusCode.PENDING,

    @ServerTimestamp
    val timestamp: Timestamp? = null
)
package festusyuma.com.glaiddriver.models

import org.threeten.bp.LocalDateTime

data class Order (
    val customer: User,
    val paymentMethod: String,
    val gasType: String,
    val gasUnit: String,
    val quantity: Double,
    val amount: Double,
    val deliveryPrice: Double,
    val tax: Double,
    var statusId: Long,
    var deliveryAddress: Address,
    val scheduledDate: LocalDateTime? = null,
    var truck: Truck? = null,
    var driverRating: Double? = null,
    var customerRating: Double? = null,
    var id: Long? = null,
    var tripStarted: LocalDateTime? = null,
    var tripEnded: LocalDateTime? = null
)
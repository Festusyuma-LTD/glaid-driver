package festusyuma.com.glaiddriver.helpers

import android.content.Context
import android.util.Log
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.models.*
import org.json.JSONArray
import org.json.JSONObject
import org.threeten.bp.LocalDateTime

class Dashboard {

    companion object {
        fun store(context: Context, data: JSONObject) {
            val dashboard = Dashboard()

            val sharedPref = context.getSharedPreferences(context.getString(R.string.cached_data), Context.MODE_PRIVATE)
            val user = gson.toJson(dashboard.getUser(data.getJSONObject("user")))
            val wallet = gson.toJson(dashboard.getWallet(data.getJSONObject("wallet")))
            val pendingOrder = dashboard.pendingOrder(data.getJSONArray("orders"))
            val orders = gson.toJson(dashboard.getOrders(data.getJSONArray("orders")))

            dashboard.getOrders(data.getJSONArray("orders"))

            with(sharedPref.edit()) {
                clear()
                putString(context.getString(R.string.sh_user_details), user)
                putString(context.getString(R.string.sh_wallet), wallet)
                putString(context.getString(R.string.sh_orders), orders)
                if (pendingOrder != null) {
                    putString(context.getString(R.string.sh_pending_order), gson.toJson(pendingOrder))
                }
                commit()
            }
        }
    }

    fun getUser(data: JSONObject): User {

        Log.v("ApiLog", "user: ${data.getString("email")}")

        return User(
            data.getString("email").capitalizeWords(),
            data.getString("fullName").capitalizeWords(),
            data.getString("tel"),
            data.getLong("id"),
            data.getDouble("rating")
        )
    }

    fun getWallet(data: JSONObject): Wallet {

        return Wallet(
            data.getDouble("wallet"),
            data.getDouble("bonus")
        )
    }

    private fun getAddress(addressJson: JSONObject): Address {
        val address = Address(
            addressJson.getLong("id"),
            addressJson.getString("address"),
            addressJson.getString("type")
        )

        val locationJson = addressJson.getJSONObject("location")
        address.lat = locationJson.getDouble("lat")
        address.lng = locationJson.getDouble("lng")

        return address
    }

    fun pendingOrder(data: JSONArray): Order? {
        for (i in 0 until data.length()) {
            val order = convertOrderJSonToOrder(data[i] as JSONObject)
            if (order.statusId < 4) {
                return order
            }
        }

        return null
    }

    fun getOrders(data: JSONArray): MutableList<Order> {
        val orders: MutableList<Order> = mutableListOf()

        for (i in 0 until data.length()) {
            orders.add(convertOrderJSonToOrder(data[i] as JSONObject))
        }

        return orders
    }

    fun convertOrderJSonToOrder(data: JSONObject): Order {
        val id: Long? = data.getLong("id")
        val quantity = data.getDouble("quantity")
        val amount = data.getDouble("amount")
        val deliveryPrice = data.getDouble("deliveryPrice")
        val tax = data.getDouble("tax")

        val scheduledDate = if (!data.isNull("scheduledDate")) {
            LocalDateTime.parse(data.getString("scheduledDate"))
        }else null

        val address = getAddress(data.getJSONObject("deliveryAddress"))
        val paymentJson = data.getJSONObject("payment")
        val paymentTypeJson = paymentJson.getString("type")
        val paymentMethod = if (paymentTypeJson == "card") {
            val paymentCard = paymentJson.getJSONObject("paymentCard")
            "Card (***** ${paymentCard.getString("carNo")})"
        }else paymentTypeJson

        val gasTypeJson = data.getJSONObject("gasType")
        val gasType = gasTypeJson.getString("type")
        val gasUnit = gasTypeJson.getString("unit")

        val statusJson = data.getJSONObject("status")
        val statusId = statusJson.getLong("id")

        val truck = if (!data.isNull("driver")) {
            val driverJson = data.getJSONObject("driver")
            val truckJson = data.getJSONObject("truck")
            val driverUser = driverJson.getJSONObject("user")

            Truck(
                driverUser.getString("fullName"),
                driverUser.getString("tel"),
                truckJson.getString("make"),
                truckJson.getString("model"),
                truckJson.getString("year"),
                truckJson.getString("color")
            )
        }else null

        val customerJson = data.getJSONObject("customer")
        val userJson = customerJson.getJSONObject("user")
        val customer = User(
            userJson.getString("email"),
            userJson.getString("fullName"),
            userJson.getString("tel"),
            userJson.getLong("id")
        )

        val driverRating = if (!data.isNull("driverRating")) {
            val rating =  data.getJSONObject("driverRating")
            rating.getDouble("userRating")
        }else null

        val customerRating = if (!data.isNull("customerRating")) {
            val rating =  data.getJSONObject("customerRating")
            rating.getDouble("userRating")
        }else null

        val order = Order(
            customer,
            paymentMethod,
            gasType,
            gasUnit,
            quantity,
            amount,
            deliveryPrice,
            tax,
            statusId,
            address,
            scheduledDate,
            truck
        )
        order.id = id
        order.driverRating = driverRating
        order.customerRating = customerRating

        return order
    }
}
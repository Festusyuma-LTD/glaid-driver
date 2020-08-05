package festusyuma.com.glaiddriver.utilities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.reflect.TypeToken
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.controller.ChatActivity
import festusyuma.com.glaiddriver.helpers.*
import festusyuma.com.glaiddriver.models.Order
import festusyuma.com.glaiddriver.models.User
import festusyuma.com.glaiddriver.models.live.PendingOrder
import festusyuma.com.glaiddriver.request.OrderRequests
import festusyuma.com.glaiddriver.requestdto.Chat
import festusyuma.com.glaiddriver.services.LocationService

class NewOrderFragment : Fragment(R.layout.fragment_new_order) {

    private lateinit var order: Order
    private lateinit var livePendingOrder: PendingOrder
    private lateinit var dataPref: SharedPreferences

    private lateinit var mainOrderMessage: TextView
    private lateinit var customerName: TextView
    private lateinit var deliverButton: Button
    private lateinit var callCustomerButton: Button
    private lateinit var textCustomerButton: Button
    private lateinit var quantity: TextView
    private lateinit var gasType: TextView
    private lateinit var failedPayment: Button
    private lateinit var user: User

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dataPref = requireActivity().getSharedPreferences(getString(R.string.cached_data), Context.MODE_PRIVATE)
        livePendingOrder = ViewModelProvider(requireActivity()).get(PendingOrder::class.java)

        val userJson = dataPref.getString(getString(R.string.sh_user_details), "null")
        if (userJson != null) {
            user = gson.fromJson(userJson, User::class.java)
        }else requireActivity().supportFragmentManager.popBackStackImmediate()

        initLivePendingOrder()
        initElem()
    }

    private fun initLivePendingOrder() {
        if (dataPref.contains(getString(R.string.sh_pending_order))) {
            val orderJson = dataPref.getString(getString(R.string.sh_pending_order), null)
            if (orderJson != null) {
                order = gson.fromJson(orderJson, Order::class.java)
                livePendingOrder.id.value = order.id
                livePendingOrder.amount.value = order.amount
                livePendingOrder.gasType.value = order.gasType
                livePendingOrder.gasUnit.value = order.gasUnit
                livePendingOrder.quantity.value = order.quantity
                livePendingOrder.truck.value = order.truck
                livePendingOrder.customer.value = order.customer
                livePendingOrder.deliveryAddress.value = order.deliveryAddress
                livePendingOrder.statusId.value = order.statusId
            }
        }
    }

    private fun initElem() {
        val orderDetails: View = requireActivity().findViewById(R.id.newOrderDetails)
        mainOrderMessage = orderDetails.findViewById(R.id.newOrderMessage)
        customerName = orderDetails.findViewById(R.id.customerName)
        callCustomerButton = orderDetails.findViewById(R.id.callCustomerButton)
        textCustomerButton = orderDetails.findViewById(R.id.textCustomerButton)
        gasType = orderDetails.findViewById(R.id.gasType)
        quantity = orderDetails.findViewById(R.id.quantity)
        deliverButton = requireActivity().findViewById(R.id.deliverButton)
        failedPayment = requireActivity().findViewById(R.id.paymentFailed)

        customerName.text =
            getString(R.string.new_order_customer_name)
                .format(livePendingOrder.customer.value?.fullName)
                .capitalizeWords()

        quantity.text =
            getString(R.string.formatted_quantity)
                .format(livePendingOrder.quantity.value, livePendingOrder.gasUnit.value)

        gasType.text = livePendingOrder.gasType.value?.capitalizeWords()
        callCustomerButton.setOnClickListener { callCustomer() }
        textCustomerButton.setOnClickListener { chat() }

        when (livePendingOrder.statusId.value) {
            OrderStatusCode.DRIVER_ASSIGNED -> deliverButton.setOnClickListener { startTrip() }
            OrderStatusCode.DELIVERED -> {
                deliverButton.text = getString(R.string.complete_delivery)
                mainOrderMessage.text = getString(R.string.starting_delivery)
                deliverButton.setOnClickListener { completeTrip() }
            }
            OrderStatusCode.PENDING_PAYMENT -> {
                deliverButton.text = getString(R.string.success)
                mainOrderMessage.text = getString(R.string.payment_pending)
                failedPayment.visibility = View.VISIBLE

                deliverButton.setOnClickListener { confirmPayment(true) }
                failedPayment.setOnClickListener { confirmPayment(false) }
            }
        }
    }

    private fun callCustomer() {
        val tel = livePendingOrder.customer.value?.tel
        if (tel != null) {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", tel.addCountryCode(), null))
            startActivity(intent)
        }
    }

    private fun chat() {
        val chatEmail = livePendingOrder.customer.value?.email
        val chatName = livePendingOrder.customer.value?.fullName

        val chatId = livePendingOrder.id.value?: return
        val sender = user.email
        val recipient = livePendingOrder.customer.value?.email?: return
        val chat = Chat(chatId.toString(), sender, recipient, true)

        if (chatEmail != null && chatName != null) {
            val intent = Intent(requireActivity(), ChatActivity::class.java)
            intent.putExtra(CHAT_NAME, chatName)
            intent.putExtra(CHAT_EMAIL, chatEmail)
            intent.putExtra(CHAT, gson.toJson(chat))
            startActivity(intent)
        }
    }

    private fun startTrip() {
        OrderRequests(requireActivity()).startTrip {
            livePendingOrder.statusId.value = OrderStatusCode.ON_THE_WAY
            deliverButton.text = getString(R.string.complete_delivery)
            mainOrderMessage.text = getString(R.string.starting_delivery)
            startLocationService()
            deliverButton.setOnClickListener { completeTrip() }
            updateLocalOrderStatus(OrderStatusCode.ON_THE_WAY)
        }
    }

    private fun completeTrip() {
        OrderRequests(requireActivity()).completeTrip {
            stopLocationService()

            if (order.paymentMethod == PaymentType.CASH) {
                deliverButton.text = getString(R.string.success)
                mainOrderMessage.text = getString(R.string.payment_pending)
                failedPayment.visibility = View.VISIBLE
                livePendingOrder.statusId.value = OrderStatusCode.PENDING_PAYMENT

                deliverButton.setOnClickListener { confirmPayment(true) }
                failedPayment.setOnClickListener { confirmPayment(false) }
                updateLocalOrderStatus(OrderStatusCode.PENDING_PAYMENT)
            }else {
                updateLocalOrderStatus(OrderStatusCode.DELIVERED)
                endPendingOrder()
            }
        }
    }

    private fun confirmPayment(success: Boolean) {
        OrderRequests(requireActivity()).confirmPayment(success) {
            failedPayment.visibility = View.GONE
            updateLocalOrderStatus(
                if (success) OrderStatusCode.DELIVERED else OrderStatusCode.FAILED
            )
            endPendingOrder()
        }
    }

    private fun endPendingOrder() {
        with(dataPref.edit()) {
            remove(getString(R.string.sh_pending_order))
            commit()
        }

        clearLivePendingOrder()

        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
            .replace(R.id.frameLayoutId, DashboardFragment())
            .commit()
    }

    private fun updateLocalOrderStatus(statusId: Long) {
        val typeToken = object: TypeToken<MutableList<Order>>(){}.type
        val ordersJson = dataPref.getString(getString(R.string.sh_orders), null)
        val orders = if (ordersJson != null) {
            gson.fromJson(ordersJson, typeToken)
        }else mutableListOf<Order>()

        orders.forEach {
            if (it.id == order.id) {
                it.statusId = statusId

                with(dataPref.edit()) {
                    putString(getString(R.string.sh_orders), gson.toJson(orders))
                    commit()
                }

                return@forEach
            }
        }
    }

    private fun clearLivePendingOrder() {
        livePendingOrder.amount.value = null
        livePendingOrder.gasType.value = null
        livePendingOrder.gasUnit.value = null
        livePendingOrder.quantity.value = null
        livePendingOrder.statusId.value = null
        livePendingOrder.truck.value = null
        livePendingOrder.customer.value = null
        livePendingOrder.deliveryAddress.value = null
    }

    private fun startLocationService() {
        if (!locationServiceRunning()) {
            Intent(requireActivity(), LocationService::class.java).also { intent ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requireActivity().startForegroundService(intent)
                }else requireActivity().startService(intent)
            }
        }
    }

    private fun stopLocationService() {
        Intent(requireActivity(), LocationService::class.java).also { intent ->
            requireActivity().stopService(intent)
        }
    }

    private fun locationServiceRunning(): Boolean {
        return false
    }
}

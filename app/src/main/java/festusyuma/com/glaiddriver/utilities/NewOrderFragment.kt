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
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.controller.ChatActivity
import festusyuma.com.glaiddriver.helpers.*
import festusyuma.com.glaiddriver.models.Order
import festusyuma.com.glaiddriver.models.live.PendingOrder
import festusyuma.com.glaiddriver.request.OrderRequests
import festusyuma.com.glaiddriver.services.LocationService

class NewOrderFragment : Fragment(R.layout.fragment_new_order) {

    private lateinit var livePendingOrder: PendingOrder
    private lateinit var dataPref: SharedPreferences

    private lateinit var mainOrderMessage: TextView
    private lateinit var customerName: TextView
    private lateinit var deliverButton: Button
    private lateinit var callCustomerButton: Button
    private lateinit var textCustomerButton: Button
    private lateinit var quantity: TextView
    private lateinit var gasType: TextView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dataPref = requireActivity().getSharedPreferences(getString(R.string.cached_data), Context.MODE_PRIVATE)
        livePendingOrder = ViewModelProvider(requireActivity()).get(PendingOrder::class.java)
        initLivePendingOrder()
        initElem()
    }

    private fun initLivePendingOrder() {
        if (dataPref.contains(getString(R.string.sh_pending_order))) {
            val orderJson = dataPref.getString(getString(R.string.sh_pending_order), null)
            if (orderJson != null) {
                val order = gson.fromJson(orderJson, Order::class.java)

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

        if (livePendingOrder.statusId.value == DRIVER_ASSIGNED_STATUS_CODE) {
            deliverButton.setOnClickListener { startTrip() }
        }else {
            deliverButton.text = getString(R.string.complete_delivery)
            mainOrderMessage.text = getString(R.string.starting_delivery)
            deliverButton.setOnClickListener { completeTrip() }
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
        val intent = Intent(requireActivity(), ChatActivity::class.java)
        startActivity(intent)
    }

    private fun startTrip() {
        OrderRequests(requireActivity()).startTrip {
            livePendingOrder.statusId.value = OrderStatusCode.ON_THE_WAY
            deliverButton.text = getString(R.string.complete_delivery)
            mainOrderMessage.text = getString(R.string.starting_delivery)
            startLocationService()
            deliverButton.setOnClickListener { completeTrip() }
        }
    }

    private fun completeTrip() {
        OrderRequests(requireActivity()).completeTrip {
            stopLocationService()

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

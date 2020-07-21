package festusyuma.com.glaiddriver.utilities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.controller.ChatActivity
import festusyuma.com.glaiddriver.helpers.API_LOG_TAG
import festusyuma.com.glaiddriver.helpers.addCountryCode
import festusyuma.com.glaiddriver.helpers.buttonClickAnim
import festusyuma.com.glaiddriver.helpers.capitalizeWords
import festusyuma.com.glaiddriver.models.live.PendingOrder
import festusyuma.com.glaiddriver.request.OrderRequests

class NewOrderFragment : Fragment(R.layout.fragment_new_order) {

    private lateinit var livePendingOrder: PendingOrder

    private lateinit var mainOrderMessage: TextView
    private lateinit var customerName: TextView
    private lateinit var deliverButton: Button
    private lateinit var callCustomerButton: Button
    private lateinit var textCustomerButton: Button
    private lateinit var quantity: TextView
    private lateinit var gasType: TextView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        livePendingOrder = ViewModelProvider(requireActivity()).get(PendingOrder::class.java)
        initElem()
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
        callCustomerButton.text = livePendingOrder.customer.value?.tel

        callCustomerButton.setOnClickListener { callCustomer(livePendingOrder.customer.value?.tel) }
        textCustomerButton.setOnClickListener { chat() }
    }

    private fun callCustomer(tel: String?) {
        if (tel != null) {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", tel.addCountryCode(), null))
            startActivity(intent)
        }
    }

    private fun chat() {
        val intent = Intent(requireActivity(), ChatActivity::class.java)
        startActivity(intent)
    }

    private fun deliveryTrip() {

    }

    val clickListener = View.OnClickListener { view ->
        view.startAnimation(buttonClickAnim)
        when (view.id) {
            R.id.callCustomerButton -> {
                //run call intent

            }
            R.id.textCustomerButton -> {

            }
            R.id.deliverButton -> {
                when (deliverButton.text) {
                    getString(R.string.start_delivery) -> {
                        /*//Start delivery Api should be initiated here
                        deliverButton.text = getString(R.string.complete_delivery)
                        mainOrderMessage.text = getString(R.string.starting_delivery)*/

                        OrderRequests(requireActivity()).startTrip {
                            Log.v(API_LOG_TAG, it.getString("message"))
                        }
                    }
                    getString(R.string.complete_delivery) -> {
                        val toast = Toast.makeText(activity, deliverButton.text, Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                        toast.show()
                        //Load the Genereate invoice page from here
                    }
                }

            }
        }
    }
}

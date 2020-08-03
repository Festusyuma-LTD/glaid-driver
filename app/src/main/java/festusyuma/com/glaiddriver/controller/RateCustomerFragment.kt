package festusyuma.com.glaiddriver.controller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.reflect.TypeToken
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.helpers.gson
import festusyuma.com.glaiddriver.models.Order
import festusyuma.com.glaiddriver.request.OrderRequests
import festusyuma.com.glaiddriver.requestdto.RatingRequest

class RateCustomerFragment : Fragment(R.layout.rate_customer_fragment) {

    private lateinit var doneBtn: ConstraintLayout
    private lateinit var ratingBar: RatingBar

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val orderJson = arguments?.getString("order")
        val order = gson.fromJson(orderJson, Order::class.java)

        ratingBar = requireActivity().findViewById(R.id.ratingBar)
        doneBtn = requireActivity().findViewById(R.id.doneBtn)
        doneBtn.setOnClickListener {
            if (ratingBar.rating <= 0) {
                OrderRequests(requireActivity()).showError("Rating cannot be 0")
            }else {
                val orderId = order.id ?: return@setOnClickListener
                val ratingRequest = RatingRequest(orderId, ratingBar.rating.toDouble())

                OrderRequests(requireActivity()).rateCustomer(ratingRequest) {
                    val dataPref = requireActivity().getSharedPreferences(
                        getString(R.string.cached_data),
                        Context.MODE_PRIVATE
                    )

                    val typeToken = object: TypeToken<MutableList<Order>>(){}.type
                    val ordersJson = dataPref.getString(getString(R.string.sh_orders), null)

                    if (ordersJson != null) {
                        val orders: MutableList<Order> = gson.fromJson(ordersJson, typeToken)
                        orders.forEach {
                            if (it.id == orderId) {
                                it.customerRating = ratingRequest.rating

                                with(dataPref.edit()) {
                                    putString(getString(R.string.sh_orders), gson.toJson(orders))
                                    commit()
                                }

                                val intent = Intent(requireActivity(), OrderDetailsActivity::class.java)
                                intent.putExtra("order", gson.toJson(it))
                                startActivity(intent)

                                return@forEach
                            }
                        }
                    }

                    requireActivity().finish()
                }
            }
        }
    }
}
package festusyuma.com.glaiddriver.utilities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.*

import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.helpers.*
import festusyuma.com.glaiddriver.models.Order
import festusyuma.com.glaiddriver.models.User
import festusyuma.com.glaiddriver.models.fs.FSPendingOrder
import festusyuma.com.glaiddriver.models.live.PendingOrder
import festusyuma.com.glaiddriver.request.OrderRequests

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private lateinit var dataPref: SharedPreferences
    private lateinit var listener: ListenerRegistration

    private lateinit var greeting: TextView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        greeting = requireActivity().findViewById(R.id.greeting)
        dataPref = requireActivity().getSharedPreferences(getString(R.string.cached_data), Context.MODE_PRIVATE)
        val userJson = dataPref.getString(getString(R.string.sh_user_details), "null")

        if (userJson != null) {
            val user = gson.fromJson(userJson, User::class.java)
            greeting.text = getString(R.string.home_greeting_intro_text).format(user.fullName.getFirst())
        }
    }

    override fun onResume() {
        super.onResume()
        startOrderListener()
        Log.v(FIRE_STORE_LOG_TAG, "started listener")
    }

    private fun startOrderListener() {
        val locationRef =
            db.collection(getString(R.string.fs_pending_orders))
                .whereEqualTo(getString(R.string.fs_pending_orders_driver_id), auth.uid?.toLong())
                .whereEqualTo(
                    getString(R.string.fs_pending_orders_status),
                    OrderStatusCode.DRIVER_ASSIGNED
                )

        listener = locationRef.addSnapshotListener(MetadataChanges.INCLUDE, getEventListener())
    }

    private fun getEventListener(): EventListener<QuerySnapshot> {

        return EventListener { values, e ->
            if (e != null) {
                Log.v(FIRE_STORE_LOG_TAG, "Error: ${e.message}")
            }

            if (values != null) {

                for (document in values.documentChanges) {
                    if (document.type == DocumentChange.Type.ADDED) {
                        val doc = document.document
                        if (!doc.metadata.isFromCache) {
                            val orderId = doc.id.toLong()
                            val status = doc.getLong(getString(R.string.fs_pending_orders_status))

                            if (status != null) {
                                if (status == OrderStatusCode.DRIVER_ASSIGNED) {
                                    OrderRequests(requireActivity()).getOrderDetails(orderId) {
                                        val order = Dashboard().convertOrderJSonToOrder(it)

                                        with(dataPref.edit()) {
                                            putString(getString(R.string.sh_pending_order), gson.toJson(order))
                                            commit()
                                        }

                                        Log.v(FIRE_STORE_LOG_TAG, "order found ${order.id}")
                                        startPendingOrderFragment()
                                    }

                                    return@EventListener
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startPendingOrderFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
            .replace(R.id.frameLayoutId, NewOrderFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onPause() {
        super.onPause()
        listener.remove()
        Log.v(FIRE_STORE_LOG_TAG, "stopped listener")
    }
}

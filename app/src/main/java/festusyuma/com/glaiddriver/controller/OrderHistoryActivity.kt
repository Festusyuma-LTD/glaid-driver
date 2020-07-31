package festusyuma.com.glaiddriver.controller

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.reflect.TypeToken
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.adapters.OrderHistoryAdapter
import festusyuma.com.glaiddriver.services.DataServices
import festusyuma.com.glaiddriver.helpers.buttonClickAnim
import festusyuma.com.glaiddriver.helpers.gson
import festusyuma.com.glaiddriver.models.Order
import kotlinx.android.synthetic.main.activity_order_history.*

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var dataPref: SharedPreferences
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)
    }

    override fun onResume() {
        super.onResume()

        dataPref = getSharedPreferences(getString(R.string.cached_data), Context.MODE_PRIVATE)
        val layoutManager = LinearLayoutManager(this)
        val typeToken = object: TypeToken<MutableList<Order>>(){}.type
        val ordersJson = dataPref.getString(getString(R.string.sh_orders), null)

        if (ordersJson != null) {
            val orders: MutableList<Order> = gson.fromJson(ordersJson, typeToken)
            orderHistoryAdapter = OrderHistoryAdapter(this, orders) {
                val orderDetails = Intent(this, OrderDetailsActivity::class.java)
                orderDetails.putExtra("order", gson.toJson(it))
                startActivity(orderDetails)
            }

            orderHistoryRecycler.layoutManager = layoutManager
            orderHistoryRecycler.adapter = orderHistoryAdapter
            // for performance when we know the layout sizes wont be changing
            orderHistoryRecycler.setHasFixedSize(true)
        }
    }

    fun helpBackClick(view: View) {
        finish()
    }
}

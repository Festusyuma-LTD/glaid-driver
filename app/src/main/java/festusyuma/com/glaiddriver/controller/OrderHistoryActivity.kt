package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.adapters.OrderHistoryAdapter
import festusyuma.com.glaiddriver.services.DataServices
import festusyuma.com.glaiddriver.utilities.buttonClickAnim
import kotlinx.android.synthetic.main.activity_order_history.*

class OrderHistoryActivity : AppCompatActivity() {
    lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        val layoutManager = LinearLayoutManager(this)
        orderHistoryAdapter = OrderHistoryAdapter(this, DataServices.orderHistoryList) {
            val productPageLink = Intent(this, OrderDetailsActivity::class.java)
//            productPageLink.putExtra(EXTRA_QUESTION, it.title)
            startActivity(productPageLink)

        }

        orderHistoryRecycler.layoutManager = layoutManager
        orderHistoryRecycler.adapter = orderHistoryAdapter
        // for performance when we know the layout sizes wont be changing
        orderHistoryRecycler.setHasFixedSize(true)
        //use beloow for chat+
//        mLayoutManager.setReverseLayout(true);
//        mLayoutManager.setStackFromEnd(true);
    }

    fun helpBackClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}

package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.utilities.buttonClickAnim

class OrderDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)
    }

    fun backClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val intent = Intent(this, OrderHistoryActivity::class.java)
        startActivity(intent)
    }

    fun viewInvoiceClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val intent = Intent(this, OrderInvoiceActivity::class.java)
        startActivity(intent)
    }

    fun rateCustomerClick(view: View) {
        view.startAnimation(buttonClickAnim)
    }
}

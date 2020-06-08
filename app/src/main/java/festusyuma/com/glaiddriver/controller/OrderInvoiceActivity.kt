package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import festusyuma.com.glaiddriver.R
import kotlinx.android.synthetic.main.activity_order_invoice.*

class OrderInvoiceActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_order_invoice)
    }

    fun downloadClick(view: View) {}
    fun backClick(view: View) {

        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
    fun confirmPayment(view: View) {
        confirmpayment.visibility = View.INVISIBLE
        paid_banner.visibility = View.VISIBLE
        confrimImg.visibility = View.INVISIBLE
        paymentStatusText.text = getString(R.string.paid)
    }
}

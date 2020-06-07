package festusyuma.com.glaiddriver.controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import festusyuma.com.glaiddriver.R
import kotlinx.android.synthetic.main.activity_order_invoice.*

class OrderInvoiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_invoice)
    }

    fun downloadClick(view: View) {}
    fun backClick(view: View) {}
    fun confirmPayment(view: View) {
        confirmpayment.visibility = View.INVISIBLE
        paid_banner.visibility = View.VISIBLE
        paymentStatusText.text = getString(R.string.paid)
    }
}

package festusyuma.com.glaiddriver.controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.helpers.PaymentType
import festusyuma.com.glaiddriver.helpers.gson
import festusyuma.com.glaiddriver.models.Order
import org.threeten.bp.format.DateTimeFormatter
import java.text.NumberFormat

class OrderInvoiceActivity : AppCompatActivity() {

    private lateinit var order: Order
    private lateinit var fee: TextView
    private lateinit var paymentType: TextView
    private lateinit var orderNumber: TextView
    private lateinit var orderDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_invoice)

        val orderJson = intent.getStringExtra("order")
        if (orderJson != null) {
            order = gson.fromJson(orderJson, Order::class.java)
        }else finish()

        initElements()
    }

    private fun initElements() {
        val numberFormatter = NumberFormat.getInstance()
        fee = findViewById(R.id.paymentAmount)
        paymentType = findViewById(R.id.paymentType)
        orderNumber = findViewById(R.id.orderNumber)
        orderDate = findViewById(R.id.orderTimeAndDate)

        val feeIs = getString(R.string.fee_is).format(numberFormatter.format(order.amount))
        val totalAmount = getString(R.string.formatted_amount).format(
            numberFormatter.format(order.amount + order.deliveryPrice + order.tax)
        )
        val delivery = getString(R.string.formatted_amount).format(
            numberFormatter.format(order.deliveryPrice)
        )
        val tax = getString(R.string.formatted_amount).format(
            numberFormatter.format(order.tax)
        )

        fee.text = totalAmount
        paymentType.text = getPaymentType()
        orderNumber.text = getOrderNumber()
        orderDate.text = order.created?.format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"))
    }

    private fun getPaymentType(): String {
        return when (order.paymentMethod) {
            PaymentType.WALLET -> PaymentType.WALLET_TEXT
            PaymentType.CASH -> PaymentType.CASH_TEXT
            else -> PaymentType.CARD_TEXT
        }
    }

    private fun getOrderNumber(): String {
        val orderNumberStr = order.id.toString()

        val orderNumberFormat = if (orderNumberStr.length < 10) {
            "0".repeat(10 - orderNumberStr.length) + orderNumberStr
        }else orderNumberStr

        return getString(R.string.order_number).format(orderNumberFormat)
    }

    fun downloadClick(view: View) {}

    fun backClick(view: View) {
        finish()
    }
}

package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.adapters.HelpSupportAdapter
import festusyuma.com.glaiddriver.services.DataServices
import festusyuma.com.glaiddriver.helpers.buttonClickAnim
import kotlinx.android.synthetic.main.activity_help_support.*

class HelpSupportActivity : AppCompatActivity() {
    lateinit var topQuestionAdapter: HelpSupportAdapter
    lateinit var paymentAdapter: HelpSupportAdapter


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
        setContentView(R.layout.activity_help_support)

        // defining an adapter using a custom recycler view
        topQuestionAdapter = HelpSupportAdapter(this, DataServices.questions) {
            val productPageLink = Intent(this, SupportFullPageActivity::class.java)
//            productPageLink.putExtra(EXTRA_QUESTION, it.title)
            startActivity(productPageLink)
        }
        paymentAdapter = HelpSupportAdapter(this, DataServices.questions) {
            val productPageLink = Intent(this, SupportFullPageActivity::class.java)
//            productPageLink.putExtra(EXTRA_QUESTION, it.title)
            startActivity(productPageLink)
        }
        val topQuestionlayoutManager = LinearLayoutManager(this)
        val paymentlayoutManager = LinearLayoutManager(this)
        topQuestionRecycler.layoutManager = topQuestionlayoutManager
        topQuestionRecycler.adapter = topQuestionAdapter
        // for performance when we know the layout sizes wont be changing
        topQuestionRecycler.setHasFixedSize(true)

        paymentRecycler.layoutManager = paymentlayoutManager
        paymentRecycler.adapter = paymentAdapter
        // for performance when we know the layout sizes wont be changing
        paymentRecycler.setHasFixedSize(true)
    }

    fun helpBackClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    fun liveChatClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
    }
}

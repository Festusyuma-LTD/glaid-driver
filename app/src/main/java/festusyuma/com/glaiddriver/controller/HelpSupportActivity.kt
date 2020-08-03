package festusyuma.com.glaiddriver.controller

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.adapters.HelpSupportAdapter
import festusyuma.com.glaiddriver.helpers.CHAT_EMAIL
import festusyuma.com.glaiddriver.helpers.CHAT_NAME
import festusyuma.com.glaiddriver.helpers.buttonClickAnim
import festusyuma.com.glaiddriver.helpers.gson
import festusyuma.com.glaiddriver.models.User
import festusyuma.com.glaiddriver.services.DataServices
import kotlinx.android.synthetic.main.activity_help_support.*

class HelpSupportActivity : AppCompatActivity() {
    lateinit var topQuestionAdapter: HelpSupportAdapter
    lateinit var paymentAdapter: HelpSupportAdapter
    lateinit var user: User

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
        setContentView(R.layout.activity_help_support)
        initDriverDetails()
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

    private fun initDriverDetails() {
        val dataPref = this.getSharedPreferences(
            getString(R.string.cached_data),
            Context.MODE_PRIVATE
        )
        val userJson = dataPref?.getString(getString(R.string.sh_user_details), "null")
        if (userJson != null) {
            user = gson.fromJson(userJson, User::class.java)
        }
    }

    fun liveChatClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(CHAT_NAME, user.fullName)
        intent.putExtra(CHAT_EMAIL, user.email)
        startActivity(intent)
    }
}

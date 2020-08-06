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
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.adapters.HelpSupportAdapter
import festusyuma.com.glaiddriver.helpers.*
import festusyuma.com.glaiddriver.models.User
import festusyuma.com.glaiddriver.requestdto.Chat
import festusyuma.com.glaiddriver.services.DataServices
import kotlinx.android.synthetic.main.activity_help_support.*

class HelpSupportActivity : AppCompatActivity() {
    private lateinit var topQuestionAdapter: HelpSupportAdapter
    private lateinit var paymentAdapter: HelpSupportAdapter
    private lateinit var user: User

    private lateinit var dataPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_support)

        dataPref = getSharedPreferences(getString(R.string.cached_data), Context.MODE_PRIVATE)
        val userJson = dataPref.getString(getString(R.string.sh_user_details), "null")
        if (userJson != null) {
            user = gson.fromJson(userJson, User::class.java)
        }else finish()

        // defining an adapter using a custom recycler view
        topQuestionAdapter = HelpSupportAdapter(this, DataServices.questions) {
            val productPageLink = Intent(this, SupportFullPageActivity::class.java)
            startActivity(productPageLink)
        }
        paymentAdapter = HelpSupportAdapter(this, DataServices.questions) {
            val productPageLink = Intent(this, SupportFullPageActivity::class.java)
            startActivity(productPageLink)
        }
        val topQuestionLayoutManager = LinearLayoutManager(this)
        val paymentLayoutManager = LinearLayoutManager(this)

        topQuestionRecycler.layoutManager = topQuestionLayoutManager
        topQuestionRecycler.adapter = topQuestionAdapter
        topQuestionRecycler.setHasFixedSize(true)
        paymentRecycler.layoutManager = paymentLayoutManager
        paymentRecycler.adapter = paymentAdapter
        paymentRecycler.setHasFixedSize(true)
    }

    fun helpBackClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    fun liveChatClick(view: View) {
        view.startAnimation(buttonClickAnim)

        val chatId = auth.uid?: return
        val sender = user.email
        val senderName = user.fullName.capitalizeWords()
        val recipient = "Support"
        val recipientName = "Support"
        val chat = Chat(
            chatId,
            sender,
            senderName,
            recipient,
            recipientName
        )

        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(CHAT_NAME, user.fullName)
        intent.putExtra(CHAT_EMAIL, user.email)
        intent.putExtra(CHAT, gson.toJson(chat))

        startActivity(intent)
    }
}

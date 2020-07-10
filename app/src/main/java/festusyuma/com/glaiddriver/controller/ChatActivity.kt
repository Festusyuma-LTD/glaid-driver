package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.adapters.ChatAdapter
import festusyuma.com.glaiddriver.services.DataServices
import festusyuma.com.glaiddriver.utilities.buttonClickAnim
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    lateinit var chatAdapter: ChatAdapter
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
        setContentView(R.layout.activity_chat)
        chatAdapter = ChatAdapter(this, DataServices.chatTestBlock)
        val layoutManager = LinearLayoutManager(this)

        //use beloow for chat+
        layoutManager.reverseLayout = false
        layoutManager.stackFromEnd = true
        chatView.layoutManager = layoutManager
        chatView.adapter = chatAdapter
        // for performance when we know the layout sizes wont be changing
        chatView.setHasFixedSize(true)

    }

    fun editBackBtnClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)

    }

    fun sendMessageClick(view: View) {
        view.startAnimation(buttonClickAnim)
        Toast.makeText(this, chatMessageBox.editableText, Toast.LENGTH_SHORT).show()
    }
}

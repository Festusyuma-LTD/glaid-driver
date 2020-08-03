package festusyuma.com.glaiddriver.controller

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.adapters.ChatRecieveItem
import festusyuma.com.glaiddriver.adapters.ChatSendItem
import festusyuma.com.glaiddriver.helpers.*
import festusyuma.com.glaiddriver.models.ChatMessage
import festusyuma.com.glaiddriver.models.User
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {
    //    lateinit var chatAdapter: ChatAdapter
    lateinit var user: User
    private val TAG = "ChatActivity"
    lateinit var apdater: GroupAdapter<GroupieViewHolder>
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
        setContentView(R.layout.activity_chat)
        initDriverDetails()
        //listen for new messages
        listenForMessages()
//        chatAdapter = ChatAdapter(this, DataServices.chatTestBlock)
        val layoutManager = LinearLayoutManager(this)

        //use beloow for chat+
        layoutManager.reverseLayout = false
        layoutManager.stackFromEnd = true
        chatView.layoutManager = layoutManager
        chatView.adapter = apdater
        // for performance when we know the layout sizes wont be changing
        chatView.setHasFixedSize(true)

    }

    private fun listenForMessages() {
        val customer_email = intent.getStringExtra(CHAT_EMAIL)!!
        apdater = GroupAdapter()
        db.collection("chat_messages/${user.email}/$customer_email")
            .orderBy("timeStamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w(TAG, "listen:error", error)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val newChat = dc.document.toObject(ChatMessage::class.java)
                            Log.d(TAG, "New message: $newChat")
                            //if message is sent by current loggin user
                            if (newChat.senderId == user.email) {
                                apdater.add(ChatSendItem(newChat))
                            } else {
                                apdater.add(ChatRecieveItem("friendDetail", newChat))
                            }
                        }
                        DocumentChange.Type.MODIFIED -> {
                            Log.d(TAG, "Modified message: ${dc.document.data}")
                        }
                        DocumentChange.Type.REMOVED -> {
                            Log.d(TAG, "Removed message: ${dc.document.data}")
                        }
                    }
                }
            }
    }

    private fun initDriverDetails() {
        val customerName: String = if (intent.getStringExtra(CHAT_EMAIL) == user.email) {
            "Customer Support"
        } else {
            intent.getStringExtra(CHAT_NAME)!!
        }
        chatTitle.text = getString(R.string.chat_user_name, customerName)
        val dataPref = this.getSharedPreferences(
            getString(R.string.cached_data),
            Context.MODE_PRIVATE
        )
        val userJson = dataPref?.getString(getString(R.string.sh_user_details), "null")
        if (userJson != null) {
            user = gson.fromJson(userJson, User::class.java)
        }
    }

    fun editBackBtnClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)

    }

    fun sendMessageClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val customer_email = intent.getStringExtra(CHAT_EMAIL)!!
        val text = chatMessageBox.text.toString()
        val senderId = user.email
        val recieverId = customer_email
        val timeStamp = Timestamp.now()
        val newChat = ChatMessage(text, senderId, recieverId, timeStamp)
        Toast.makeText(this, chatMessageBox.editableText, Toast.LENGTH_SHORT).show()
        db.collection("chat_messages/$senderId/$recieverId").add(newChat)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "senderId DocumentSnapshot written with ID: $documentReference ")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
        db.collection("messages/$recieverId/$senderId").add(newChat)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "recieverId DocumentSnapshot written with ID: $documentReference ")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}

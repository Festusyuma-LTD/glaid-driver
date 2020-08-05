package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.adapters.ChatReceiveItem
import festusyuma.com.glaiddriver.adapters.ChatSendItem
import festusyuma.com.glaiddriver.helpers.*
import festusyuma.com.glaiddriver.models.ChatMessage
import festusyuma.com.glaiddriver.models.User
import festusyuma.com.glaiddriver.models.fs.FSChatMessage
import festusyuma.com.glaiddriver.requestdto.Chat
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {
    //    lateinit var chatAdapter: ChatAdapter
    private var user: User? = null
    private val TAG = "ChatActivity"
    lateinit var apdater: GroupAdapter<GroupieViewHolder>

    private lateinit var chat: Chat
    private lateinit var chatView: RecyclerView
    private lateinit var chatMessageBox: EditText

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
        setContentView(R.layout.activity_chat)

        chatView = findViewById(R.id.chatView)
        chatMessageBox = findViewById(R.id.chatMessageBox)

        val chatJson = intent.getStringExtra(CHAT)
        if (chatJson != null) {
            chat = gson.fromJson(chatJson, Chat::class.java)
        }else finish()

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
        val customerEmail = if (intent.getStringExtra(CHAT_EMAIL) == user?.email) {
            "customerCare@glaidDriver.com"
        } else {
            intent.getStringExtra(CHAT_EMAIL)!!
        }
        apdater = GroupAdapter()
        db.collection("chat_log/${user?.email}/$customerEmail")
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
                            if (newChat.senderId == user?.email) {
                                apdater.add(ChatSendItem(newChat))
                            } else {
                                apdater.add(ChatReceiveItem("friendDetail", newChat))
                            }
                            //scrool recycler to new message
                            chatView.scrollToPosition(apdater.itemCount - 1)
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
        user = initDriverDetails(this)
        chatTitle.text = if (intent.getStringExtra(CHAT_EMAIL) == user?.email) {
            "Customer Support"
        } else {
            getString(R.string.chat_user_name, intent.getStringExtra(CHAT_NAME)!!.getFirst())
        }

    }

    fun editBackBtnClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)

    }

    fun sendMessageClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val message = chatMessageBox.text.toString()
        if (message.isBlank()) return
        val chatMessage = FSChatMessage(chat.sender, message)

        db.collection(getString(R.string.fs_chat_rooms))
            .document(chat.chatRoomId)
            .collection(
                if (chat.isOrder) {
                    getString(R.string.fs_order_messages)
                } else getString(R.string.fs_support_messages)
            )
            .add(chatMessage)
            .addOnSuccessListener {
                Log.v(FIRE_STORE_LOG_TAG, "message sent")
            }
            .addOnFailureListener {
                Log.v(FIRE_STORE_LOG_TAG, "error sending message ${it.message}")
            }

        chatMessageBox.text.clear()
        chatView.scrollToPosition(apdater.itemCount - 1)

        /*val receiverId = if (intent.getStringExtra(CHAT_EMAIL) == user?.email) {
            "customerCare@glaidDriver.com"
        } else {
            intent.getStringExtra(CHAT_EMAIL)!!
        }
        val senderId = user!!.email
        val timeStamp = Timestamp.now()
        val dateFormat: DateFormat = SimpleDateFormat("hh:mm aa", Locale.getDefault())
        val dateString: String = dateFormat.format(Date()).toString()
        val newChat =
            ChatMessage(
                chatMessageBox.editableText.toString(),
                senderId,
                receiverId,
                timeStamp,
                dateString
            )
        chatMessageBox.editableText.clear()
//        Toast.makeText(this, chatMessageBox.editableText, Toast.LENGTH_SHORT).show()
        db.collection("chat_log/$senderId/$receiverId").add(newChat)
            .addOnSuccessListener {
                Log.d(TAG, "$senderId mmessage to $receiverId log: $newChat")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
        db.collection("chat_log/$receiverId/$senderId").add(newChat)
            .addOnSuccessListener {
                Log.d(TAG, "$senderId mmessage to $receiverId log: $newChat")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        //scrool recycler to new message*/
    }

}

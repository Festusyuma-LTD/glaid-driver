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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.adapters.ChatAdapter
import festusyuma.com.glaiddriver.models.User
import festusyuma.com.glaiddriver.services.DataServices
import festusyuma.com.glaiddriver.utilities.NewOrderFragment
import festusyuma.com.glaiddriver.utilities.buttonClickAnim
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chatbox_reciever.view.*
import kotlinx.android.synthetic.main.chatbox_sender.view.*
import java.util.*

class ChatActivity : AppCompatActivity() {

    lateinit var chatAdapter: ChatAdapter
    lateinit var fireBaseDBref: DatabaseReference

    val newAdapter = GroupAdapter<GroupieViewHolder>()

    @ExperimentalStdlibApi
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
        //get user Intent Extre
        val userObject = intent.getParcelableExtra<User>(NewOrderFragment.USER_DATA)

        //L I S T E N F O R N E W M E S S A G E
        listenForNewMessage()
        //set chat user name
        val chatTitleText = "Chat With " + userObject?.username?.capitalize(Locale.ROOT)
        chatTitle.text = chatTitleText
        //add chat adapter
        chatAdapter = ChatAdapter(this, DataServices.chatTestBlock)

//        newAdapter.add(ChatFromItem(this, DataServices.chatTestBlock))
//        newAdapter.add(ChatToItem(this, DataServices.chatTestBlock))
        val layoutManager = LinearLayoutManager(this)


        //use beloow for chat+
        layoutManager.reverseLayout = false
        layoutManager.stackFromEnd = true
        chatView.layoutManager = layoutManager
        chatView.adapter = newAdapter
        // for performance when we know the layout sizes wont be changing
        chatView.setHasFixedSize(true)

    }

    private fun listenForNewMessage() {
        fireBaseDBref = FirebaseDatabase.getInstance().getReference("/chat_messages")
        fireBaseDBref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    applicationContext,
                    "failed to retrieve messages: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            /**
             * This method is triggered when a child location's priority changes. See [ ][DatabaseReference.setPriority] and [Ordered Data](https://firebase.google.com/docs/database/android/retrieve-data#data_order) for more information on priorities and ordering data
             */
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            /**
             * This method is triggered when the data at a child location has changed.
             */
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            /**
             * This method is triggered when a new child is added to the location to which this listener was
             * added.
             */
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //save the data to a class of your choosing and access the calue with it
                val newChat = snapshot.getValue(SendChat::class.java) ?: return
                //check if user id to sort messages to adapter
                val userObject =
                    intent.getParcelableExtra<User>(NewOrderFragment.USER_DATA) ?: return
                Log.d(
                    "checkIds",
                    ":::login user =>${FirebaseAuth.getInstance().uid}\n:::message sender=>${newChat.fromId}\n:::other user =>${userObject.uid}message recieved from other=>${newChat.toId}"
                )
                if (FirebaseAuth.getInstance().uid == newChat.fromId) {
                    newAdapter.add(ChatToItem(applicationContext, newChat))
                }
                if (userObject.uid == newChat.toId) {
                    newAdapter.add(ChatFromItem(applicationContext, newChat))
                }
            }

            /**
             * This method is triggered when a child is removed from the location to which this listener was
             * added.
             */
            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

        })

    }

    fun editBackBtnClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)

    }

    fun sendMessageClick(view: View) {
        view.startAnimation(buttonClickAnim)
        performSendOperation()
    }

    private fun performSendOperation() {
        //message body
        if (chatMessageBox.text == null) return
        val messageBody = chatMessageBox.editableText.toString()
        //get user Intent Extre
        val userObject = intent.getParcelableExtra<User>(NewOrderFragment.USER_DATA)

        fireBaseDBref = FirebaseDatabase.getInstance().getReference("/chat_messages").push()
        val messageTime = System.currentTimeMillis() / 1000
        //change this to custom user id
        val fromId = FirebaseAuth.getInstance().uid
        val toId = userObject?.uid ?: "no_to_user_id"
        //set Id of user chatting to


        fireBaseDBref.setValue(
            SendChat(
                fireBaseDBref.key!!,
                messageBody,
                messageTime,
                fromId ?: "no_from_user_id",
                toId
            )
        )
            .addOnSuccessListener {
                chatMessageBox.text.clear()

            }
    }

    // class from person replying you
    class ChatFromItem(
        private val contextInstance: Context,
        private val chat: SendChat
    ) : Item<GroupieViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.chatbox_sender
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//            val resourceId =
//                contextInstance.resources.getIdentifier(
//                    chat.,
//                    "drawable",
//                    contextInstance.packageName
//                )
//            viewHolder.itemView.senderImg?.setImageResource(resourceId)
            viewHolder.itemView.sender_chatbox.text = chat.messageBody
            viewHolder.itemView.senderTime.text = chat.messageTime.toString()

        }

    }
// class for person typing and sending message
    class ChatToItem(
        val contextInstance: Context,
        val chat: SendChat
    ) : Item<GroupieViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.chatbox_reciever
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.reciever_chatbox.text = chat.messageBody
            viewHolder.itemView.reciever_time.text = chat.messageTime.toString()
        }

    }

    class SendChat(
        val id: String,
        val messageBody: String,
        val messageTime: Long,
        val fromId: String,
        val toId: String
    ) {
        constructor() : this("", "", -1, "", "")
    }
}

package festusyuma.com.glaiddriver.models

data class Chat (
    val chatRoomId: String,
    val sender: String,
    val senderName: String,
    val recipient: String,
    val recipientName: String,
    val isOrder: Boolean = false
)
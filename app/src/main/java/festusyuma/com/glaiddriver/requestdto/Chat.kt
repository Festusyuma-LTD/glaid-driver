package festusyuma.com.glaiddriver.requestdto

data class Chat (
    val chatRoomId: String,
    val sender: String,
    val recipient: String,
    val isOrder: Boolean = false
)
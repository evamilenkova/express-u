package mk.ukim.finki.expressu.model

import com.google.firebase.Timestamp

data class ChatMessage(
    val message: String = "",
    val senderId: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
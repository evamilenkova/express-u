package mk.ukim.finki.expressu.model

import com.google.firebase.Timestamp

data class ChatMessage(
    val originalMessage: String = "",
    val translatedMessage: String = "",
    val senderId: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val photoUrl: String? = null
)
package mk.ukim.finki.expressu.model

import com.google.firebase.Timestamp

data class Chatroom(
    val id: String = "",
    val usersIds: List<String> = listOf(),
    var lastMessageTimeStamp: Timestamp? = null,
    var lastMessageSenderId: String? = null,
    var lastMessage: String? = null,
    var lastMessageTranslated: String? = null,
    var photoSend: Boolean? = null
)
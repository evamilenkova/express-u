package mk.ukim.finki.expressu.model

import com.google.firebase.Timestamp

data class Chatroom(
    val id: String = "",
    val usersIds: List<String> = listOf(),
    var lastMessage: Timestamp? = null,
    var lastMessageSenderId: String? = null
) 
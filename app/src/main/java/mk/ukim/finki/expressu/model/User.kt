package mk.ukim.finki.expressu.model

import com.google.firebase.Timestamp

data class User(
    val id: String = "0",
    val phone: String = "",
    var username: String = "",
    val createdOn: Timestamp = Timestamp.now()
)

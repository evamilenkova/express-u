package mk.ukim.finki.expressu.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseUtil {

    companion object {
        fun currentUserId(): String? {
            return FirebaseAuth.getInstance().uid
        }

        fun currentUserDetails(): DocumentReference? {
            return currentUserId()?.let {
                FirebaseFirestore.getInstance().collection("users").document(it)
            }
        }

        fun isLoggedIn(): Boolean {
            return currentUserId() != null
        }

        fun allUsers(): CollectionReference {
            return FirebaseFirestore.getInstance().collection("users")
        }

        fun getChatroom(chatroomId: String): DocumentReference {
            return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId)
        }

        fun getChatroomId(userId1: String, userId2: String): String{
            return if( userId1.hashCode()<userId2.hashCode()){
                "${userId1}_${userId2}"
            } else {
                "${userId2}_${userId1}"
            }
        }

        fun getChatRoomMessage(chatroomId: String): CollectionReference{
       return getChatroom(chatroomId).collection("chats")
        }
    }
}
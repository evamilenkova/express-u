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
    }
}
package mk.ukim.finki.expressu.utils

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Locale

class FirebaseUtil {
    companion object {

        private val firestore: FirebaseFirestore by lazy {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            firestoreInstance.firestoreSettings = settings
            firestoreInstance
        }

        fun initialize() {
            // Ensure Firestore instance is initialized with the settings
            firestore
        }

        fun currentUserId(): String? {
            return FirebaseAuth.getInstance().uid
        }

        fun currentUserDetails(): DocumentReference? {
            return currentUserId()?.let {
                firestore.collection("users").document(it)
            }
        }

        fun getUserFromId(id: String): DocumentReference {
            return allUsers().document(id)
        }

        fun isLoggedIn(): Boolean {
            return currentUserId() != null
        }

        fun allUsers(): CollectionReference {
            return firestore.collection("users")
        }

        fun getChatroom(chatroomId: String): DocumentReference {
            return firestore.collection("chatrooms").document(chatroomId)
        }

        fun getChatroomId(userId1: String, userId2: String): String {
            return if (userId1.hashCode() < userId2.hashCode()) {
                "${userId1}_${userId2}"
            } else {
                "${userId2}_${userId1}"
            }
        }

        fun getChatRoomMessage(chatroomId: String): CollectionReference {
            return getChatroom(chatroomId).collection("chats")
        }

        fun getAllChatRooms(): CollectionReference {
            return firestore.collection("chatrooms")
        }

        fun timeStampToString(timestamp: Timestamp): String {
            return SimpleDateFormat("HH:mm", Locale.FRANCE).format(timestamp.toDate())
        }

        fun getCurrentProfilePicStorageRef(): StorageReference {
            return FirebaseStorage.getInstance().reference.child("profile_pic")
                .child(currentUserId()!!)
        }

        fun getProfilePicStorageRef(id: String): StorageReference {
            return FirebaseStorage.getInstance().reference.child("profile_pic")
                .child(id)
        }

        fun getChatImagesStorageRef(name: String): StorageReference {
            return FirebaseStorage.getInstance().reference.child("chat_images")
                .child(name)
        }

        fun logout() {
            FirebaseAuth.getInstance().signOut()
        }
    }
}
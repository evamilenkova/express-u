package mk.ukim.finki.expressu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import mk.ukim.finki.expressu.adapter.ChatRecyclerAdapter
import mk.ukim.finki.expressu.adapter.UserContactRecyclerAdapter
import mk.ukim.finki.expressu.model.ChatMessage
import mk.ukim.finki.expressu.model.Chatroom
import mk.ukim.finki.expressu.model.User
import mk.ukim.finki.expressu.utils.AndroidUtil
import mk.ukim.finki.expressu.utils.FirebaseUtil

class ChatActivity : AppCompatActivity() {

    private lateinit var otherUser: User
    private lateinit var chatroomId: String
    private lateinit var messagesRecylcerView: RecyclerView
    private var chatroom: Chatroom? = null
    private lateinit var currentUserId: String
    private lateinit var sendMessageButton: ImageButton
    private lateinit var messageInput: EditText
    private lateinit var chatMessageAdapter: ChatRecyclerAdapter
    private lateinit var userPhoto: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        currentUserId = FirebaseUtil.currentUserId()!!
        otherUser = AndroidUtil.getUserFromIntent(intent)
        chatroomId = FirebaseUtil.getChatroomId(currentUserId, otherUser.id)
        sendMessageButton = findViewById(R.id.sendMessageBtn)
        messageInput = findViewById(R.id.chat_message)
        messagesRecylcerView = findViewById(R.id.messages_recycler_view)
        val username: TextView = findViewById(R.id.chat_username)
        val backBtn: ImageButton = findViewById(R.id.back_btn)
        userPhoto = findViewById(R.id.profile_pic)
        //other code , what code?


        username.text = otherUser.username
        backBtn.setOnClickListener {
            onBackPressed()
        }

        FirebaseUtil.getProfilePicStorageRef(otherUser.id).downloadUrl.addOnCompleteListener { t ->
            if (t.isSuccessful) {
                AndroidUtil.setProfilePic(this, t.result, userPhoto)
            }
        }

        this.sendMessageButton.setOnClickListener {
            val message = this.messageInput.text.toString().trim()
            if (!message.isEmpty()) sendMessageToUser(message)
        }

        getOrCreateChatroomModel();
        setUpChatRecyclerView();
    }


    fun setUpChatRecyclerView() {
        val query = FirebaseUtil.getChatRoomMessage(chatroomId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val options: FirestoreRecyclerOptions<ChatMessage> =
            FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage::class.java)
                .build()

        chatMessageAdapter = ChatRecyclerAdapter(options, context = applicationContext)
        val manager = LinearLayoutManager(this)
        manager.reverseLayout = true
        messagesRecylcerView.layoutManager = manager
        messagesRecylcerView.adapter = chatMessageAdapter
        chatMessageAdapter.startListening()
        chatMessageAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                messagesRecylcerView.smoothScrollToPosition(0)
            }
        })
    }

    fun sendMessageToUser(message: String) {
        chatroom?.lastMessageTimeStamp = Timestamp.now()
        chatroom?.lastMessageSenderId = currentUserId
        chatroom?.lastMessage = message
        FirebaseUtil.getChatroom(chatroomId).set(chatroom!!)
        val chatMessage = ChatMessage(message, currentUserId)

        FirebaseUtil.getChatRoomMessage(chatroomId).add(chatMessage).addOnCompleteListener {
            if (it.isSuccessful) {
                messageInput.setText("")
            }
        }
    }

    fun getOrCreateChatroomModel() {
        FirebaseUtil.getChatroom(chatroomId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                chatroom = it.result.toObject(Chatroom::class.java)

                if (chatroom == null) {
                    chatroom =
                        Chatroom(
                            chatroomId, listOf(currentUserId, otherUser.id),
                            Timestamp.now(),
                            ""
                        );

                    FirebaseUtil.getChatroom(chatroomId).set(chatroom!!)
                }
            }
        }
    }
}
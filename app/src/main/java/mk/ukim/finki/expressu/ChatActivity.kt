package mk.ukim.finki.expressu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import mk.ukim.finki.expressu.adapter.ChatRecyclerAdapter
import mk.ukim.finki.expressu.databinding.ActivityChatBinding
import mk.ukim.finki.expressu.fragments.WrapContentLinearLayoutManager
import mk.ukim.finki.expressu.model.ChatMessage
import mk.ukim.finki.expressu.model.Chatroom
import mk.ukim.finki.expressu.model.User
import mk.ukim.finki.expressu.translationUtils.TranslatorRepository
import mk.ukim.finki.expressu.utils.AndroidUtil
import mk.ukim.finki.expressu.utils.FirebaseUtil
import mk.ukim.finki.expressu.utils.NotificationUtil
import org.json.JSONObject
import java.util.Locale

class ChatActivity : AppCompatActivity() {

    private var _binding: ActivityChatBinding? = null


    private lateinit var chatMessageAdapter: ChatRecyclerAdapter
    private val binding get() = _binding!!
    private lateinit var otherUser: User
    private lateinit var currentUser: User
    private lateinit var chatroomId: String
    private lateinit var currentUserId: String
    private lateinit var messagesRecyclerView: RecyclerView
    private var chatroom: Chatroom? = null


    private lateinit var imagePickLauncher: ActivityResultLauncher<Intent>
    private lateinit var speechResultLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUserId = FirebaseUtil.currentUserId()!!
        otherUser = AndroidUtil.getUserFromIntent(intent)
        chatroomId = FirebaseUtil.getChatroomId(currentUserId, otherUser.id)

        messagesRecyclerView = binding.messagesRecyclerView
        val username: TextView = findViewById(R.id.chat_username)
        val backBtn: ImageButton = findViewById(R.id.back_btn)



        FirebaseUtil.currentUserDetails()?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                currentUser = task.result.toObject(User::class.java)!!
            }
        }

        username.text = otherUser.phone
        backBtn.setOnClickListener {
            onBackPressed()
        }

        FirebaseUtil.getProfilePicStorageRef(otherUser.id).downloadUrl.addOnCompleteListener { t ->
            if (t.isSuccessful) {
                AndroidUtil.setProfilePic(this, t.result, binding.profilePictureChat)
            }
        }


        binding.microphone.setOnClickListener {
            startSpeechToText()
        }

        speechResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    val results =
                        result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    results?.let {
                        binding.chatMessage.setText(it[0].toString())
                    }
                }
            }

        binding.sendPhoto.setOnClickListener {
            ImagePicker.with(this).cropSquare().compress(512).createIntent {
                imagePickLauncher.launch(it)
            }
        }

        binding.sendMessageBtn.setOnClickListener {
            val message = binding.chatMessage.text.toString().trim()
            if (message.isNotEmpty()) sendMessageToUser(message)
        }

        imagePickLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data != null && data.data != null) {
                        data.data?.let {
                            val photoName = it.lastPathSegment
                            FirebaseUtil.getChatImagesStorageRef(photoName.toString()).putFile(it)
                                .addOnCompleteListener {
                                    val chatMessage = ChatMessage(
                                        senderId = currentUserId,
                                        photoUrl = photoName
                                    )
                                    chatroom?.photoSend = true
                                    saveChatMessage(chatMessage)
                                    FirebaseUtil.getChatroom(chatroomId).set(chatroom!!)
                                }
                        }
                    }
                }
            }

        getOrCreateChatroomModel()
        setUpChatRecyclerView()
    }

    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        if (intent.resolveActivity(packageManager) != null) {
            speechResultLauncher.launch(intent)
        } else {
            AndroidUtil.showToast(this, "Speech recognition not available")
        }
    }


    private fun setUpChatRecyclerView() {
        val query = FirebaseUtil.getChatRoomMessage(chatroomId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val options: FirestoreRecyclerOptions<ChatMessage> =
            FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage::class.java)
                .build()

        chatMessageAdapter = ChatRecyclerAdapter(options, applicationContext)
        val manager = WrapContentLinearLayoutManager(this)
        manager.reverseLayout = true
        messagesRecyclerView.layoutManager = manager
        messagesRecyclerView.adapter = chatMessageAdapter
        chatMessageAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                messagesRecyclerView.smoothScrollToPosition(0)
            }
        })
    }

    private fun sendMessageToUser(message: String) {
        chatroom?.lastMessageTimeStamp = Timestamp.now()
        chatroom?.lastMessageSenderId = currentUserId
        chatroom?.lastMessage = message
        chatroom?.photoSend = false


        if (otherUser.language != currentUser.language) {
            TranslatorRepository.translateText(
                message,
                otherUser.language
            ) { translatedMessage ->
                val chatMessage = ChatMessage(
                    originalMessage = message,
                    translatedMessage = translatedMessage ?: "",
                    senderId = currentUserId
                )
                chatroom?.lastMessageTranslated = translatedMessage ?: ""
                saveChatMessage(chatMessage)
                FirebaseUtil.getChatroom(chatroomId).set(chatroom!!)
                //sendNotification(translatedMessage ?: message)
            }
        } else {
            val chatMessage = ChatMessage(
                originalMessage = message,
                senderId = currentUserId
            )
            saveChatMessage(chatMessage)
            //sendNotification(message)
            FirebaseUtil.getChatroom(chatroomId).set(chatroom!!)
        }
    }

    private fun saveChatMessage(chatMessage: ChatMessage) {
        FirebaseUtil.getChatRoomMessage(chatroomId).add(chatMessage).addOnCompleteListener {
            if (it.isSuccessful) {
                binding.chatMessage.setText("")
            }
        }
    }

    private fun getOrCreateChatroomModel() {
        FirebaseUtil.getChatroom(chatroomId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                chatroom = it.result.toObject(Chatroom::class.java)
                if (currentUserId != chatroom?.lastMessageSenderId && chatroom?.lastMessageSeen == false) {
                    FirebaseUtil.updateSeenStatus(chatroomId, true)
                }
                if (chatroom == null) {
                    chatroom =
                        Chatroom(
                            chatroomId, listOf(currentUserId, otherUser.id),
                            Timestamp.now(),
                            ""
                        )

                    FirebaseUtil.getChatroom(chatroomId).set(chatroom!!)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        chatMessageAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        chatMessageAdapter.stopListening()
    }

    private fun sendNotification(sendMessage: String) {
        try {
            val json = JSONObject().apply {
                val notification = JSONObject().apply {
                    put("title", currentUser.username)
                    put("body", sendMessage)
                }

                val data = JSONObject().apply {
                    put("userId", currentUserId)
                }

                val message = JSONObject().apply {
                    put("notification", notification)
                    put("data", data)
                    put("token", otherUser.fcmToken)
                }

                put("message", message)
            }

            NotificationUtil.callAPI(json)
        } catch (_: Exception) {
        }
    }

    companion object {
        private const val REQUEST_CODE_SPEECH_INPUT = 100
    }

}



package mk.ukim.finki.expressu.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import mk.ukim.finki.expressu.ChatActivity
import mk.ukim.finki.expressu.R
import mk.ukim.finki.expressu.model.Chatroom
import mk.ukim.finki.expressu.model.User
import mk.ukim.finki.expressu.utils.AndroidUtil
import mk.ukim.finki.expressu.utils.FirebaseUtil

class RecentChatRecyclerAdapter(
    options: FirestoreRecyclerOptions<Chatroom>,
    private val context: Context
) :
    FirestoreRecyclerAdapter<Chatroom, RecentChatRecyclerAdapter.ChatroomViewHolder>(options) {

    class ChatroomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.username_text)
        val message: TextView = itemView.findViewById(R.id.last_message_text)
        val time: TextView = itemView.findViewById(R.id.last_message_text_time)
        val photo: ImageView = itemView.findViewById(R.id.profile_pic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatroomViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recent_chat_recycler_item, parent, false)

        return ChatroomViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: ChatroomViewHolder,
        position: Int,
        chatroom: Chatroom
    ) {
        val currentUserId = FirebaseUtil.currentUserId()
        val otherUserId = if (chatroom.usersIds[0] == currentUserId) {
            chatroom.usersIds[1]
        } else {
            chatroom.usersIds[0]
        }
        FirebaseUtil.getUserFromId(otherUserId)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val sendByMe = chatroom.lastMessageSenderId == currentUserId

                    val model = task.result.toObject(User::class.java)
                    FirebaseUtil.getProfilePicStorageRef(model!!.id).downloadUrl.addOnCompleteListener { t ->
                        if (t.isSuccessful) {
                            AndroidUtil.setProfilePic(context, t.result, holder.photo)
                        }
                    }
                    val message =
                        if (sendByMe) chatroom.lastMessage else chatroom.lastMessageTranslated.takeIf {
                            it?.isNotEmpty() ?: false
                        }
                            ?: chatroom.lastMessage


                    val displayedMessage = when {
                        chatroom.photoSend == true -> "Photo attachment"
                        message != null -> message.substring(0, minOf(25, message.length))
                        else -> "No message available"
                    }

                    holder.name.text = model.username
                    holder.message.text =
                        if (sendByMe) "You: $displayedMessage" else displayedMessage
                    holder.time.text =
                        FirebaseUtil.timeStampToString(chatroom.lastMessageTimeStamp!!)

                    if (!sendByMe && !chatroom.lastMessageSeen) {
                        holder.message.setTypeface(null, Typeface.BOLD)
                    } else {
                        holder.message.setTypeface(null, Typeface.NORMAL)
                    }
                    holder.itemView.setOnClickListener {

                        val intent = Intent(context, ChatActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        AndroidUtil.passUserAsIntent(intent, model)
                        if (context is Activity) {
                            context.startActivity(intent)
                        } else {
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.applicationContext.startActivity(intent)
                        }
                    }
                }
            }
    }

}
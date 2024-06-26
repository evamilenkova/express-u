package mk.ukim.finki.expressu.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextClock
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import mk.ukim.finki.expressu.ChatActivity
import mk.ukim.finki.expressu.R
import mk.ukim.finki.expressu.model.ChatMessage
import mk.ukim.finki.expressu.model.User
import mk.ukim.finki.expressu.utils.AndroidUtil
import mk.ukim.finki.expressu.utils.FirebaseUtil

class ChatRecyclerAdapter(
    options: FirestoreRecyclerOptions<ChatMessage>,
    private val context: Context
) :
    FirestoreRecyclerAdapter<ChatMessage, ChatRecyclerAdapter.ChatMessageViewHolder>(options) {

    class ChatMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val leftChatLayout: LinearLayout = itemView.findViewById(R.id.left_chat)
        val rightChatLayout: LinearLayout = itemView.findViewById(R.id.right_chat)
        val leftChatText: TextView = itemView.findViewById(R.id.left_chat_text)
        val rightChatText: TextView = itemView.findViewById(R.id.right_chat_text)
        val rightImage: ImageView = itemView.findViewById(R.id.right_message_image)
        val leftImage: ImageView = itemView.findViewById(R.id.left_message_image)
        fun bind(message: ChatMessage) {
            if (message.senderId == FirebaseUtil.currentUserId()) {
                leftChatLayout.visibility = View.GONE
                rightChatLayout.visibility = View.VISIBLE
                rightChatText.text = message.originalMessage
                message.photoUrl?.let {
                    rightImage.visibility = View.VISIBLE
                    rightChatText.visibility = View.GONE
                    FirebaseUtil.getChatImagesStorageRef(message.photoUrl).downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            AndroidUtil.setChatImage(rightImage.context, task.result, rightImage)
                        }
                    }
                }

            } else {
                rightChatLayout.visibility = View.GONE
                leftChatLayout.visibility = View.VISIBLE
                leftChatText.text =
                    message.translatedMessage.ifBlank { message.originalMessage }
            }
            message.photoUrl?.let {
                leftImage.visibility = View.VISIBLE
                leftChatText.visibility = View.GONE
                FirebaseUtil.getChatImagesStorageRef(message.photoUrl).downloadUrl.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        AndroidUtil.setChatImage(leftImage.context, task.result, leftImage)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_message_item, parent, false)

        return ChatMessageViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: ChatMessageViewHolder,
        position: Int,
        model: ChatMessage
    ) {
        holder.bind(model)
    }
}
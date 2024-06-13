package mk.ukim.finki.expressu.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import mk.ukim.finki.expressu.ChatActivity
import mk.ukim.finki.expressu.R
import mk.ukim.finki.expressu.SearchUserActivity
import mk.ukim.finki.expressu.model.User
import mk.ukim.finki.expressu.utils.AndroidUtil
import mk.ukim.finki.expressu.utils.FirebaseUtil

class UserContactRecyclerAdapter(
    options: FirestoreRecyclerOptions<User>,
    private val context: Context
) :
    FirestoreRecyclerAdapter<User, UserContactRecyclerAdapter.UserContactViewHolder>(options) {

    class UserContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.username_text)
        val phone: TextView = itemView.findViewById(R.id.phone_text)
        val photo: ImageView = itemView.findViewById(R.id.profile_pic)
        fun bind(user: User) {

            if (user.id == FirebaseUtil.currentUserId()) {
                name.text = "${user.username} (Me)"
            } else {
                name.text = user.username
            }
            phone.text = user.phone
            // image view bind too :)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserContactViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_user_item, parent, false)

        return UserContactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserContactViewHolder, position: Int, model: User) {
        holder.bind(model)

        FirebaseUtil.getProfilePicStorageRef(model.id).downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                AndroidUtil.setProfilePic(context, task.result, holder.photo)
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            AndroidUtil.passUserAsIntent(intent, model)
            context.startActivity(intent)
        }
    }
}
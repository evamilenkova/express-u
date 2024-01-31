package mk.ukim.finki.expressu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import mk.ukim.finki.expressu.model.User
import mk.ukim.finki.expressu.utils.AndroidUtil

class ChatActivity : AppCompatActivity() {

    private lateinit var otherUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        otherUser = AndroidUtil.getUserFromIntent(intent)

        val username: TextView = findViewById(R.id.chat_username)
        val backBtn: ImageButton = findViewById(R.id.back_btn)
        //other code

        username.text = otherUser.username
        backBtn.setOnClickListener {
            onBackPressed()
        }
    }
}
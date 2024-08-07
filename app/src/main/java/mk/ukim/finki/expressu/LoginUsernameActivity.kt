package mk.ukim.finki.expressu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.Timestamp
import mk.ukim.finki.expressu.databinding.ActivityLoginUsernameBinding
import mk.ukim.finki.expressu.model.User
import mk.ukim.finki.expressu.utils.FirebaseUtil

class LoginUsernameActivity : AppCompatActivity() {

    private var _binding: ActivityLoginUsernameBinding? = null
    private val binding get() = _binding!!
    private var user: User? = null
    private var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginUsernameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phoneNumber = intent.getStringExtra("phone")
        getUsername()

        binding.loginFinishBtn.setOnClickListener {
            setUsername()
        }


    }

    private fun getUsername() {
        setProgressBar(true)
        FirebaseUtil.currentUserDetails()?.get()?.addOnCompleteListener { task ->
            setProgressBar(false)
            if (task.isSuccessful) {
                user = task.result?.toObject(User::class.java)
                user?.let {
                    binding.loginUsername.setText(it.username)
                }
            }
        }
    }

    private fun setUsername() {
        setProgressBar(true)
        val username = binding.loginUsername.text.toString()
        if (username.isEmpty() || username.length < 3) {
            binding.loginUsername.error = "Username length should be at least 3 characters"
            return
        }
        if (user != null) {
            user!!.username = username
        } else {
            val id = FirebaseUtil.currentUserId() ?: "1"
            user = User(id, phoneNumber!!, username, Timestamp.now())
        }

        FirebaseUtil.currentUserDetails()?.set(user!!)?.addOnCompleteListener { task ->
            setProgressBar(false)
            if (task.isSuccessful) {
                val intent = Intent(this@LoginUsernameActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun setProgressBar(isVisible: Boolean) {
        binding.loginFinishBtn.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.loginProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}
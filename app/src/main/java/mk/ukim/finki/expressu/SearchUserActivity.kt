package mk.ukim.finki.expressu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.hbb20.CountryCodePicker
import mk.ukim.finki.expressu.adapter.UserContactRecyclerAdapter
import mk.ukim.finki.expressu.databinding.ActivityLoginPhoneNumberBinding
import mk.ukim.finki.expressu.databinding.ActivitySearchUserBinding
import mk.ukim.finki.expressu.fragments.WrapContentLinearLayoutManager
import mk.ukim.finki.expressu.model.User
import mk.ukim.finki.expressu.utils.FirebaseUtil

class SearchUserActivity : AppCompatActivity() {

    private var _binding: ActivitySearchUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var userAdapter: UserContactRecyclerAdapter;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchUsernameField: EditText = binding.searchUsername
        searchUsernameField.requestFocus();


        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.searchUserBtn.setOnClickListener {
            val searchName = searchUsernameField.text.toString()
            if (searchName.isEmpty()) {
                searchUsernameField.error = "Invalid Username"
            } else {
                setUpSearchRecycleView(searchName);
            }
        }
    }

    fun setUpSearchRecycleView(searchTerm: String) {
        setProgressBar(true)
        val query = if (searchTerm.trim().any { it.isLetter() }) {
            FirebaseUtil.allUsers().whereEqualTo("username", searchTerm)
        } else {
            FirebaseUtil.allUsers().whereEqualTo("phone", searchTerm)
        }

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                setProgressBar(false)
                val users = task.result.toObjects(User::class.java)
                if (users.isNotEmpty()) {
                    binding.noUsersFound.visibility = View.GONE
                    binding.usersRecycleView.visibility = View.VISIBLE
                    val options: FirestoreRecyclerOptions<User> =
                        FirestoreRecyclerOptions.Builder<User>()
                            .setQuery(query, User::class.java)
                            .build()

                    userAdapter = UserContactRecyclerAdapter(options, context = applicationContext)
                    binding.usersRecycleView.layoutManager = WrapContentLinearLayoutManager(this)
                    binding.usersRecycleView.adapter = userAdapter
                    userAdapter.startListening()
                } else {
                    Log.d("Users", "No users found")
                    binding.noUsersFound.visibility = View.VISIBLE
                    binding.usersRecycleView.visibility = View.GONE
                }
            }
        }
    }

    fun setProgressBar(isVisible: Boolean) {
        runOnUiThread() {
            binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        if (::userAdapter.isInitialized) userAdapter.stopListening()
    }
}
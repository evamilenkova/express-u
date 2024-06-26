package mk.ukim.finki.expressu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import mk.ukim.finki.expressu.fragments.ChatFragment
import mk.ukim.finki.expressu.fragments.ProfileFragment
import mk.ukim.finki.expressu.translationUtils.LanguageManager
import mk.ukim.finki.expressu.translationUtils.TranslatorRepository
import mk.ukim.finki.expressu.utils.FirebaseUtil


class MainActivity : AppCompatActivity() {
//    private var _binding: ActivityMainBinding? = null
//    private val binding get() = _binding!!

    private val chatFragmentTag = "ChatFragmentTag"
    private var currentFragmentTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        LanguageManager.fetchLanguages(TranslatorRepository) {
            Log.i("LANG", "loaded successfully")
        }

        super.onCreate(savedInstanceState)
//        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            openChatFragment()
        }

        val bar: BottomNavigationView = findViewById(R.id.main_bottom_bar)
        val search: ImageView = findViewById(R.id.main_search_btn)

        bar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_chat -> {
                    openChatFragment()
                    true
                }

                R.id.menu_profile -> {
                    openProfileFragment()
                    true
                }

                else -> false
            }
        }

        search.setOnClickListener { v ->
            startActivity(
                Intent(
                    this@MainActivity,
                    SearchUserActivity::class.java
                )
            )
        }

        getFCMToken()
    }

    fun openChatFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, ChatFragment(), chatFragmentTag).commit()
        currentFragmentTag = chatFragmentTag
    }

    fun openProfileFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, ProfileFragment()).commit()
    }

    override fun onResume() {
        super.onResume()

        // Check if the ChatFragment is not added and add it
//        val chatFragment = supportFragmentManager.findFragmentByTag(chatFragmentTag)
//        if (chatFragment == null) {
//            openChatFragment()
//        }
    }


    fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                FirebaseUtil.currentUserDetails()?.update("fcmToken", task.result)
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }


}
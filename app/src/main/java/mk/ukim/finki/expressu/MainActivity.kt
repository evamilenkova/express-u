package mk.ukim.finki.expressu

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import mk.ukim.finki.expressu.fragments.ChatFragment
import mk.ukim.finki.expressu.fragments.ProfileFragment


class MainActivity : AppCompatActivity() {
//    private var _binding: ActivityMainBinding? = null
//    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        val bar: BottomNavigationView = findViewById(R.id.main_bottom_bar)
        val search: ImageView = findViewById(R.id.main_search_btn)

        bar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_chat -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, ChatFragment()).commit()
                    true
                }

                R.id.menu_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, ProfileFragment()).commit()
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

    }
}
package mk.ukim.finki.expressu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import mk.ukim.finki.expressu.model.User
import mk.ukim.finki.expressu.utils.AndroidUtil
import mk.ukim.finki.expressu.utils.FirebaseUtil

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseUtil.initialize()
        setContentView(R.layout.activity_splash)

        if (FirebaseUtil.isLoggedIn() && intent.extras != null) {

            // from notification
            val userId = intent.getStringExtra("userId")
            userId?.let {
                FirebaseUtil.getUserFromId(it).get().addOnCompleteListener { task ->
                    run {
                        if (task.isSuccessful) {

                            val mainIntent = Intent(this, MainActivity::class.java)
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            startActivity(mainIntent)
                            val user = task.result.toObject(User::class.java)
                            val intent = Intent(this, ChatActivity::class.java)
                            AndroidUtil.passUserAsIntent(intent, user!!)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        } else {
            Handler().postDelayed({
                if (FirebaseUtil.isLoggedIn()) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    startActivity(Intent(this, LoginPhoneNumberActivity::class.java))
                }
                finish()
            }, 500)
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        }


    }
}
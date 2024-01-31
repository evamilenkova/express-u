package mk.ukim.finki.expressu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import mk.ukim.finki.expressu.utils.FirebaseUtil

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(Runnable {
            if(FirebaseUtil.isLoggedIn()){
                startActivity(Intent(this,MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginPhoneNumberActivity::class.java))
            }
            finish()
        }, 1000)   }


}
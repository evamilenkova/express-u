package mk.ukim.finki.expressu.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import mk.ukim.finki.expressu.model.User

class AndroidUtil {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun passUserAsIntent(intent: Intent, user: User) {
            intent.putExtra("username", user.username)
            intent.putExtra("phone", user.phone)
            intent.putExtra("userId", user.id)
        }

        fun getUserFromIntent(intent: Intent): User {
            return User(
                intent.getStringExtra("userId")!!,
                intent.getStringExtra("username")!!,
                intent.getStringExtra("phone")!!
            )
        }
    }

}
package mk.ukim.finki.expressu.utils

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import android.widget.Toast
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import mk.ukim.finki.expressu.model.User

class AndroidUtil {

    companion object {
        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun passUserAsIntent(intent: Intent, user: User) {
            intent.putExtra("username", user.username)
            intent.putExtra("phone", user.phone)
            intent.putExtra("userId", user.id)
            intent.putExtra("language", user.language)
            intent.putExtra("token", user.fcmToken)
        }

        fun getUserFromIntent(intent: Intent): User {
            return User(
                intent.getStringExtra("userId")!!,
                intent.getStringExtra("username")!!,
                intent.getStringExtra("phone")!!,
                language = intent.getStringExtra("language")!!,
                fcmToken = intent.getStringExtra("token")!!
            )
        }

        fun setProfilePic(context: Context, imageUri: Uri, imageView: ImageView) {
            Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform())
                .into(imageView)
        }

        fun setChatImage(context: Context, imageUri: Uri, imageView: ImageView) {
            Glide.with(context)
                .load(imageUri)
                .apply(
                    RequestOptions.bitmapTransform(
                        RoundedCornersTransformation(
                            70,
                            0,
                            RoundedCornersTransformation.CornerType.ALL
                        )
                    )
                )
                .into(imageView)
        }
    }

}
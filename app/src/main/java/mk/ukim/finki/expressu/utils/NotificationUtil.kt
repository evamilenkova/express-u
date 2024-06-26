package mk.ukim.finki.expressu.utils

import android.util.Log
import com.google.android.gms.common.api.Response
import mk.ukim.finki.expressu.AccessToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


object NotificationUtil {



    fun callAPI(jsonObject: JSONObject) {
        val url = "https://fcm.googleapis.com/v1/projects/expressu-backend/messages:send"

        val accessToken = AccessToken().getAccessToken()

        val client = OkHttpClient()
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                println(response.body?.string())
            }
        })
//        val mediaType = "application/json; charset=utf-8".toMediaType()
//        val client = OkHttpClient()
//        val accessToken = AccessToken().getAccessToken()
//        val url = "https://fcm.googleapis.com/v1/projects/expressu-backend/messages:send"
//        val body = RequestBody.create(mediaType, jsonObject.toString())
//        val request = Request.Builder().url(url)
//            .post(body)
//            .header("authorization", "Bearer $accessToken")
//            .header("content-type", "application/json").build()
//
//
//        client.newCall(request).enqueue(object : Callback {
//
//            override fun onFailure(call: Call, e: IOException) {
//                Log.e("NOTIFICATION", "failed")
//            }
//
//            override fun onResponse(call: Call, response: okhttp3.Response) {
//                Log.e("NOTIFICATION", "${response.toString()}")
//            }

   //     })
    }
}
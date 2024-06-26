package mk.ukim.finki.expressu.translationUtils

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object TranslatorRepository {

    private val service: TranslatorService

    init {
        val logging = HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.cognitive.microsofttranslator.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(TranslatorService::class.java)
    }

    fun translateText(
        text: String,
        targetLanguage: String,
        callback: (String?) -> Unit
    ) {
        val request = listOf(TranslateRequest(text))
        val call = service.translateText( targetLanguage, request)
        call.enqueue(object : Callback<List<TranslateResponse>> {
            override fun onResponse(
                call: Call<List<TranslateResponse>>,
                response: Response<List<TranslateResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val translatedText =
                        response.body()?.firstOrNull()?.translations?.firstOrNull()?.text
                    callback(translatedText)
                } else {
                    Log.e(
                        "TranslatorRepository",
                        "Error response: ${response.errorBody()?.string()}"
                    )
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<TranslateResponse>>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun getAvailableLanguages(callback: (Map<String, Language>?) -> Unit) {
        val call = service.getLanguages()
        call.enqueue(object : Callback<LanguagesResponse> {
            override fun onResponse(
                call: Call<LanguagesResponse>,
                response: Response<LanguagesResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback(response.body()?.translation)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<LanguagesResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

}

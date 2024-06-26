package mk.ukim.finki.expressu.translationUtils

import mk.ukim.finki.expressu.BuildConfig
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface TranslatorService {
    @Headers(
        "Ocp-Apim-Subscription-Key: ${BuildConfig.AZURE_TRANSLATOR_KEY}",
        "Ocp-Apim-Subscription-Region: westeurope",
        "Content-Type: application/json",
    )
    @POST("/translate?api-version=3.0")
    fun translateText(
        @Query("to") to: String,
        @Body requestBody: List<TranslateRequest>
    ): Call<List<TranslateResponse>>

    @GET("/languages?api-version=3.0")
    fun getLanguages(): Call<LanguagesResponse>
}

data class TranslateRequest(val text: String)

data class TranslateResponse(
    val translations: List<Translation>
)

data class Translation(
    val text: String,
    val to: String
)


data class LanguagesResponse(
    val translation: Map<String, Language>
)

data class Language(
    val name: String,
    val nativeName: String,
    val dir: String
)
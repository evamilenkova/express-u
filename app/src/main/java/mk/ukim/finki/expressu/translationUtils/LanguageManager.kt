package mk.ukim.finki.expressu.translationUtils


object LanguageManager {
    data class Language(val code: String, val name: String)

    private var languages: List<Language> = emptyList()

    fun fetchLanguages(repository: TranslatorRepository, callback: (List<Language>?) -> Unit) {
        repository.getAvailableLanguages { languages ->
            languages?.let {
                this.languages = it.map { entry ->
                    Language(entry.key, entry.value.name)
                }
                callback(this.languages)
            } ?: run {
                callback(null)
            }
        }
    }


    fun getLanguages(): List<Language> {
        return languages
    }

    // Function to get language names from fetched languages
    fun getLanguageNames(): List<String> {
        return languages.map { it.name }
    }
}
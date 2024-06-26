package mk.ukim.finki.expressu

import com.google.auth.oauth2.GoogleCredentials
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class AccessToken {
    val firebaseMessage = "https://www.googleapis.com/auth/firebase.messaging";

    fun getAccessToken(): String {
        try{
            val jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"expressu-backend\",\n" +
                    "  \"private_key_id\": \"42a4c9f0ee74d3883fd8342fb2a3943624ed6e29\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDJF8+3fKse9DrO\\nMdVhK0QXG+qHLkzUKVSVrdq6sl9PXwzZh0FqosOKZdoI6w15iPMhn5hvYsnrB8jY\\nnJH/JYD/EiZ6Vhuop6zuz+pvy6I/3jVJI4puBlZH7cf1TcpnwZl7Q2ao8ofQthC4\\nUhPoP3/wPizPwEwPTkzmQNXCH8y/xon4+ciR8gSdZb+vNsjpuNuotgB1dOHCva0k\\nbGvTdd88Uw/+8uuwqDDRBUZyYrL2/u160sXvLObuaL+ibNPn9i9GM06x35hKunwL\\ntTj11K6Jcu3bYpDbmtyxshIL0KH1X9JdjW57dDu2d1J9bBcJudAvwLewsIzwYsB8\\nLcv+KMI/AgMBAAECggEAAJ/7UZr96SW3J7w4k98PjMbVxBUdFgJe30GKuXkjxK+1\\n7th1wpVo6Cgy35jjZ3+edHn2KPp0nYSzrMCnz37YAh2V/bFWJVWyuuHt3OppbFYO\\nHQES8A6YUWGSyoIIYVQqXEYJrqK6UKgQMJJzZLhgdxL/uVGOqvQtAauCk+IyFQfR\\nLKt/vA6SmqoeduTTFQiZbG4a69/S7Yb9CXotzCJF3YaL0CO/8Px8+zzOTVY1g1Bx\\n1fZifv5IyHPl0UBA2PzaNFNAUDkd6kQvxuQ2UtkK+U3JVCyEzGZveZsaJ6GYEsUU\\ngGGLvOfuWkqJNRh/U3IrczGKikF7WFC7HUHfRwwr0QKBgQDzw+F9mXuDrpJRr/jc\\n06zArblA6wOjY4+TrCT5qAlBWCz1Bi8ijBZUUss9M80MO3DOqT5Kq+nEtMKR+B1g\\n2jaSOKJlLRpR/YuRZMZKiv+uwytW7tSxOO4FPkmFGicvDI0iaafwtHkt9moxjgpA\\nKh7SO6CIZ6FNCWboLUvmtgs9OQKBgQDTL6O5uM9V5fTJUdZ2h5Lc7wKXWBC07BB6\\n7VLVE3Qo1CESJiVOtkF07fG2GyqUej8pnpsHJcpT/vk3iLPhyCezCTj16TSNDBzY\\niZ43PswBbwNWcz12knWxRW63mLvWK6r2Ln6wvpka4N9AZyoA19wQxTXjSQg1rr6N\\nUxbafS5zNwKBgAhjMgwPTueuBwzWgDqg8e3DTH+GVp61m0gagT8x3/emoA1iRBbt\\nOQt3udSGRsTn+q6xthn9plKFYbiCEAi4GW6YkkXm8mPl1HqRPB1ugPOzdXdNSnY6\\nnj4B8aepybVdujF37zZiD1VciDTiTUn96QeU06Y94pfy/d55SIZMt0BJAoGAHl/R\\nupqcJ4ylgnck03N4ahJuWvotnPf+/s83hLIBmQW5o/SclQ+dpoW6qCiMHULf5sqL\\nHaIP0bjwe05MIQM6woNnD+d0yXyegX55pRSBHwuqfTqh8nXqO47y97EeAxXQxwu0\\n6RLgzn7dXVP5NJnXawoSUBhm9+GkQota1B4BGk0CgYEAslvlDImMbGduNTuBMGeI\\n31/CDBzvnEvKsGypAk3P+rarsSwvLC1WdZkqfnCGy8CgW0OaqdcA4gx3UWmK9Meg\\n19MutYehySMppucfFBrIYso8manqk/Q57bLli3nrW5To9YNjPC424e2qPfosCw1J\\nbK6iTLb9Bi26XTzA+iqUN2E=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"expressu-backend@appspot.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"106765283564388445837\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/expressu-backend%40appspot.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}"

            val stream = ByteArrayInputStream(jsonString.encodeToByteArray())
            val googleCredentials = GoogleCredentials.fromStream(stream).createScoped(listOf(firebaseMessage))

            googleCredentials.refresh()
            return googleCredentials.accessToken.tokenValue.toString()

        } catch (_: Exception) {

        }
        return  ""
    }
}
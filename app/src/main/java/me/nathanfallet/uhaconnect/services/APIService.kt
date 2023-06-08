package me.nathanfallet.uhaconnect.services

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import me.nathanfallet.uhaconnect.models.Notification
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.utils.SingletonHolder

class APIService {

    // Constants

    companion object : SingletonHolder<APIService, Unit>({ APIService() }) {
        private const val baseUrl = "https://uhaconnect.nathanfallet.me"
    }

    // Client

    @OptIn(ExperimentalSerializationApi::class)
    private val httpClient = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            })
        }
    }

    private suspend fun createRequest(
        method: HttpMethod,
        url: String,
        token: String? = null,
        builder: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse {
        return httpClient.request(baseUrl + url) {
            this.method = method
            token?.let { token ->
                this.header("Authorization", "Bearer $token")
            }
            builder()
        }
    }

    @Throws(Exception::class)
    suspend fun getMe(token: String): User {
        return createRequest(HttpMethod.Get, "/users/me", token).body()
    }

    suspend fun getNotification(token: String): List<Notification> {
        return createRequest(HttpMethod.Get, "/notifications", token).body()
    }

}



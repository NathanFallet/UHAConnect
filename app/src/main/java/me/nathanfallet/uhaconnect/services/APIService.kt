package me.nathanfallet.uhaconnect.services

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import me.nathanfallet.uhaconnect.models.LoginPayload
import me.nathanfallet.uhaconnect.models.Post
import me.nathanfallet.uhaconnect.models.RegisterPayload
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.models.UserToken
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
    suspend fun getUser(id:Int, token: String): User {
        return createRequest(HttpMethod.Get, "/users/$id", token).body()
    }
    suspend fun getUserPosts(id:Int, token: String): List<Post> {
        return createRequest(HttpMethod.Get, "/users/$id/post", token).body()
    }

    @Throws(Exception::class)
    suspend fun login(payload: LoginPayload): UserToken? {
        return createRequest(HttpMethod.Post, "/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }.body()
    }

    @Throws(Exception::class)
    suspend fun createAccount(payload: RegisterPayload): UserToken? {
        return createRequest(HttpMethod.Post, "/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }.body()
    }

    /*suspend fun resetPassword(email: String): UserToken? {
        val payload = ResetPasswordPayload(email)
        val json = Json.encodeToString(payload)

        return try {
            val response = createRequest(HttpMethod.Post, "/auth/reset-password") {
                contentType(ContentType.Application.Json)
                setBody(json)
            }
            response.body()
        } catch (exception: Exception) {
            null
        }
    }*/

}

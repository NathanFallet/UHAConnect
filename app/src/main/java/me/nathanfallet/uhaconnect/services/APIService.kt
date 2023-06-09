package me.nathanfallet.uhaconnect.services

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import me.nathanfallet.uhaconnect.models.Comment
import me.nathanfallet.uhaconnect.models.CreateCommentPayload
import me.nathanfallet.uhaconnect.models.CreatePostPayload
import me.nathanfallet.uhaconnect.models.Favorite
import me.nathanfallet.uhaconnect.models.Follow
import me.nathanfallet.uhaconnect.models.LoginPayload
import me.nathanfallet.uhaconnect.models.MediaPayload
import me.nathanfallet.uhaconnect.models.Notification
import me.nathanfallet.uhaconnect.models.NotificationsTokenPayload
import me.nathanfallet.uhaconnect.models.Post
import me.nathanfallet.uhaconnect.models.RegisterPayload
import me.nathanfallet.uhaconnect.models.ResetPasswordPayload
import me.nathanfallet.uhaconnect.models.UpdatePostPayload
import me.nathanfallet.uhaconnect.models.UpdateUserPayload
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.models.UserToken
import me.nathanfallet.uhaconnect.utils.SingletonHolder

class APIService {

    // Constants

    companion object : SingletonHolder<APIService, Unit>({ APIService() }) {
        const val baseUrl = "https://uhaconnect.nathanfallet.me"
    }


    // Client

    @OptIn(ExperimentalSerializationApi::class)
    private val httpClient = HttpClient {
        expectSuccess = true
        install(Logging)
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
    
    @Throws(Exception::class)
    suspend fun getUser(id:Int, token: String): User {
        return createRequest(HttpMethod.Get, "/users/$id", token).body()
    }

    @Throws(Exception::class)
    suspend fun getUserPosts(id: Int, token: String, offset: Long = 0): List<Post> {
        return createRequest(HttpMethod.Get, "/users/$id/posts", token) {
            parameter("offset", offset)
        }.body()
    }

    @Throws(Exception::class)
    suspend fun getPost(token: String, id: Int): Post {
        return createRequest(HttpMethod.Get, "/posts/$id", token).body()
    }

    @Throws(Exception::class)
    suspend fun postPost(token: String, payload: CreatePostPayload): Post {
        return createRequest(HttpMethod.Post, "/posts", token) {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }.body()
    }

    @Throws(Exception::class)
    suspend fun postComment(token: String, id: Int, payload: CreateCommentPayload): Comment {
        return createRequest(HttpMethod.Post, "/posts/$id/comments", token) {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }.body()
    }

    @Throws(Exception::class)
    suspend fun login(payload: LoginPayload): UserToken {
        return createRequest(HttpMethod.Post, "/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }.body()
    }

    @Throws(Exception::class)
    suspend fun createAccount(payload: RegisterPayload): UserToken {
        return createRequest(HttpMethod.Post, "/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }.body()
    }
    @Throws(Exception::class)
    suspend fun uploadMedia(
        token: String,
        media: ByteArray,
        isVideo: Boolean
    ): MediaPayload {
        return createRequest(HttpMethod.Post, "/media", token) {
            contentType(if (isVideo) ContentType.Video.MP4 else ContentType.Image.JPEG)
            setBody(media)
        }.body()
    }

    @Throws(Exception::class)
    suspend fun getNotifications(token: String, offset: Long = 0): List<Notification> {
        return createRequest(HttpMethod.Get, "/notifications", token) {
            parameter("offset", offset)
        }.body()
    }

    @Throws(Exception::class)
    suspend fun sendNotificationToken(token: String, notificationToken: String) {
        createRequest(HttpMethod.Post, "/notifications", token) {
            contentType(ContentType.Application.Json)
            setBody(NotificationsTokenPayload(notificationToken))
        }
    }

    @Throws(Exception::class)
    suspend fun getPosts(token: String, offset: Long = 0): List<Post> {
        return createRequest(HttpMethod.Get, "/posts", token) {
            parameter("offset", offset)
        }.body()
    }

    @Throws(Exception::class)
    suspend fun getPostsRequests(token: String, offset: Long = 0): List<Post> {
        return createRequest(HttpMethod.Get, "/posts/requests", token) {
            parameter("offset", offset)
        }.body()
    }

    @Throws(Exception::class)
    suspend fun getPostsFollowing(token: String, offset: Long = 0): List<Post> {
        return createRequest(HttpMethod.Get, "/posts/follow", token) {
            parameter("offset", offset)
        }.body()
    }

    @Throws(Exception::class)
    suspend fun getComments(token: String, id: Int, offset: Long = 0): List<Comment> {
        return createRequest(HttpMethod.Get, "/posts/$id/comments", token) {
            parameter("offset", offset)
        }.body()
    }

    @Throws(Exception::class)
    suspend fun updatePost(token: String, id: Int, payload: UpdatePostPayload): Post {
        return createRequest(HttpMethod.Put, "/posts/$id", token) {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }.body()
    }

    @Throws(Exception::class)
    suspend fun deletePost(token: String, id: Int) {
        createRequest(HttpMethod.Delete, "/posts/$id", token)
    }

    @Throws(Exception::class)
    suspend fun deleteComment(token: String, idPost: Int, idComment: Int) {
        createRequest(HttpMethod.Delete, "/posts/$idPost/comments/$idComment", token)
    }

    @Throws(Exception::class)
    suspend fun getFavorites(token: String, offset: Long = 0): List<Post> {
        return createRequest(HttpMethod.Get, "/favorites", token) {
            parameter("offset", offset)
        }.body()
    }

    suspend fun addToFavorites(token: String, id: Int): Favorite {
        return createRequest(HttpMethod.Post, "/favorites/$id", token).body()
    }

    suspend fun deleteToFavorites(token: String, id: Int) {
        createRequest(HttpMethod.Delete, "/favorites/$id", token)
    }

    suspend fun updateUser(token: String, id: Int, payload: UpdateUserPayload): User {
        return createRequest(HttpMethod.Put, "/users/$id", token) {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }.body()
    }

    suspend fun resetPassword(payload: ResetPasswordPayload) {
        createRequest(HttpMethod.Post, "/auth/reset") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
    }

    suspend fun follow(token: String, id: Int): Follow {
        return createRequest(HttpMethod.Post, "/users/$id/follow", token).body()
    }

    suspend fun unfollow(token: String, id: Int) {
        createRequest(HttpMethod.Delete, "/users/$id/follow", token)
    }

    suspend fun getFollowing(token: String, id: Int, offset: Long = 0): List<User>{
        return createRequest(HttpMethod.Get, "/users/$id/following", token) {
            parameter("offset", offset)
        }.body()
    }

    suspend fun getFollowers(token: String, id: Int, offset: Long = 0): List<User>{
        return createRequest(HttpMethod.Get, "/users/$id/followers", token) {
            parameter("offset", offset)
        }.body()
    }
}



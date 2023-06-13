package me.nathanfallet.uhaconnect.plugins

import com.google.auth.oauth2.GoogleCredentials
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.nathanfallet.uhaconnect.database.Database
import me.nathanfallet.uhaconnect.models.Follows
import me.nathanfallet.uhaconnect.models.NotificationsTokens
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.select
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths

object NotificationsPlugin {

    private val credentials: GoogleCredentials? =
        if (Files.exists(Paths.get("firebase-adminsdk.json"))) GoogleCredentials
            .fromStream(FileInputStream("firebase-adminsdk.json"))
            .createScoped("https://www.googleapis.com/auth/firebase.messaging")
        else null

    @OptIn(ExperimentalSerializationApi::class)
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            })
        }
    }

    private suspend fun sendNotification(notification: NotificationPayload) {
        if (credentials == null) return
        val token = credentials.refreshAccessToken()
        httpClient.post("https://fcm.googleapis.com/v1/projects/uhaconnect/messages:send") {
            header("Authorization", "Bearer ${token.tokenValue}")
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "message" to notification
                )
            )
        }
    }

    fun sendNotificationFromPayload(notification: NotificationPayload) {
        CoroutineScope(Job()).launch {
            sendNotification(notification)
        }
    }

    fun sendNotificationToUser(userId: Int, notification: NotificationData) {
        CoroutineScope(Job()).launch {
            Database.dbQuery {
                NotificationsTokens
                    .select { NotificationsTokens.userId eq userId }
                    .map { it[NotificationsTokens.token] }
            }.forEach { token ->
                sendNotification(
                    NotificationPayload(
                        token = token,
                        notification = notification
                    )
                )
            }
        }
    }

    fun sendNotificationToFollowers(userId: Int, notification: NotificationData) {
        CoroutineScope(Job()).launch {
            Database.dbQuery {
                NotificationsTokens
                    .join(Follows, JoinType.INNER, Follows.follower_id, NotificationsTokens.userId)
                    .select { Follows.user_id eq userId }
                    .map { it[NotificationsTokens.token] }
            }.forEach { token ->
                sendNotification(
                    NotificationPayload(
                        token = token,
                        notification = notification
                    )
                )
            }
        }
    }

    fun sendNotificationToTopic(topic: String, notification: NotificationData) {
        CoroutineScope(Job()).launch {
            sendNotification(
                NotificationPayload(
                    topic = topic,
                    notification = notification
                )
            )
        }
    }

}

@Serializable
data class NotificationPayload(
    val token: String? = null,
    val topic: String? = null,
    val notification: NotificationData
)

@Serializable
data class NotificationData(
    val title: String? = null,
    val body: String? = null,
    val title_loc_key: String? = null,
    val body_loc_key: String? = null,
    val title_loc_args: List<String>? = null,
    val body_loc_args: List<String>? = null
)

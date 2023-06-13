package me.nathanfallet.uhaconnect.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import me.nathanfallet.uhaconnect.database.Database
import me.nathanfallet.uhaconnect.models.Notifications
import me.nathanfallet.uhaconnect.models.NotificationsTokenPayload
import me.nathanfallet.uhaconnect.models.NotificationsTokens
import me.nathanfallet.uhaconnect.models.Users
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

fun Route.apiNotifications() {
    route("/notifications") {
        get {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "User not found"))
                return@get
            }
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0L
            val notifications = Database.dbQuery {
                Notifications
                    .join(Users, JoinType.INNER, Notifications.origin_id, Users.id)
                    .select { Notifications.dest_id eq user.id }
                    .orderBy(Notifications.date, SortOrder.DESC)
                    .limit(limit, offset)
                    .map(Notifications::toNotification)
            }
            call.respond(notifications)
        }
        post {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@post
            }
            val token = try {
                call.receive<NotificationsTokenPayload>()
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Missing data"))
                return@post
            }
            val expire =
                Clock.System.now().plus(1, DateTimeUnit.YEAR, TimeZone.currentSystemDefault())
            Database.dbQuery {
                try {
                    NotificationsTokens.insert {
                        it[NotificationsTokens.token] = token.token
                        it[NotificationsTokens.userId] = user.id
                        it[NotificationsTokens.expiration] = expire.toEpochMilliseconds()
                    }
                } catch (e: Exception) {
                    NotificationsTokens.update({
                        NotificationsTokens.token eq token.token
                    }) {
                        it[NotificationsTokens.userId] = user.id
                        it[NotificationsTokens.expiration] = expire.toEpochMilliseconds()
                    }
                }
            }
            call.response.status(HttpStatusCode.Created)
        }
    }
}
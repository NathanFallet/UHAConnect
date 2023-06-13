package me.nathanfallet.uhaconnect.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import me.nathanfallet.uhaconnect.database.Database
import me.nathanfallet.uhaconnect.models.*
import me.nathanfallet.uhaconnect.plugins.NotificationData
import me.nathanfallet.uhaconnect.plugins.NotificationsPlugin
import org.jetbrains.exposed.sql.*

fun Route.apiFavorites() {
    route("/favorites") {
        get {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "User not found"))
                return@get
            }
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0L
            val favorites = Database.dbQuery {
                Posts
                    .join(Users, JoinType.INNER)
                    .join(Favorites, JoinType.INNER, Posts.id, Favorites.post_id)
                    .select { Favorites.user_id eq user.id }
                    .orderBy(Posts.date, SortOrder.DESC)
                    .limit(limit, offset)
                    .map(Posts::toPost)
            }
            call.respond(favorites)
        }

        post("/{id}") {
            val post = call.parameters["id"]?.toIntOrNull()?.let { id ->
                Database.dbQuery {
                    Posts
                        .select { Posts.id eq id }
                        .map(Posts::toPost)
                        .singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid post id"))
                return@post
            }
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "User not found"))
                return@post
            }
            val favorite = Database.dbQuery {
                Favorites
                    .select { Favorites.post_id eq post.id and (Favorites.user_id eq user.id) }
                    .map(Favorites::toFavorite)
                    .singleOrNull() ?: Favorites.insert {
                    it[Favorites.user_id] = user.id
                    it[Favorites.post_id] = post.id
                }.resultedValues?.map(Favorites::toFavorite)?.singleOrNull()
            } ?: run {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(mapOf("error" to "Error while adding post to favorite."))
                return@post
            }

            Database.dbQuery {
                Notifications
                    .insert {
                        it[Notifications.dest_id] = post.user_id
                        it[Notifications.post_id] = post.id
                        it[Notifications.type] = TypeStatus.LIKE.toString()
                        it[Notifications.origin_id] = user.id
                    }
            }
            NotificationsPlugin.sendNotificationToUser(
                post.user_id,
                NotificationData(
                    title_loc_key = "notification_like",
                    title_loc_args = listOf(user.username),
                    body = post.title
                )
            )

            call.response.status(HttpStatusCode.Created)
            call.respond(favorite)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: run {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid post id"))
                return@delete
            }
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "User not found"))
                return@delete
            }
            Database.dbQuery {
                Favorites.deleteWhere {
                    Op.build { (Favorites.post_id eq id) and (Favorites.user_id eq user.id) }
                }
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
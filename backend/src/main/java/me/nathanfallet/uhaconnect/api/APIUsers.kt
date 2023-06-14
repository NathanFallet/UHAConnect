package me.nathanfallet.uhaconnect.api

import at.favre.lib.crypto.bcrypt.BCrypt
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.datetime.Clock
import me.nathanfallet.uhaconnect.database.Database
import me.nathanfallet.uhaconnect.models.Favorites
import me.nathanfallet.uhaconnect.models.Follows
import me.nathanfallet.uhaconnect.models.Notifications
import me.nathanfallet.uhaconnect.models.Permission
import me.nathanfallet.uhaconnect.models.Posts
import me.nathanfallet.uhaconnect.models.TypeStatus
import me.nathanfallet.uhaconnect.models.UpdateUserPayload
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.models.Users
import me.nathanfallet.uhaconnect.plugins.NotificationData
import me.nathanfallet.uhaconnect.plugins.NotificationsPlugin
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

fun Route.apiUsers() {
    route("/users") {
        get {
            val users = Database.dbQuery {
                Users.selectAll()
                    .map { Users.toUser(it) }
            }
            call.respond(users)
        }
        get("/me") {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@get
            }
            call.respond(user)
        }
        get("/{id}") {
            val me = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@get
            }
            val id = call.parameters["id"]?.toIntOrNull() ?: run {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid user id"))
                return@get
            }
            val user = Database.dbQuery {
                Users
                    .join(Follows, JoinType.LEFT) {
                        Follows.user_id eq Users.id and (Follows.follower_id eq null or (Follows.follower_id eq me.id))
                    }
                    .select { Users.id eq id }
                    .map { Users.toUser(it) }
                    .singleOrNull()
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(mapOf("error" to "User not found"))
                return@get
            }
            call.respond(user)
        }
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: run {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid user id"))
                return@put
            }
            val user = Database.dbQuery {
                Users
                    .select { Users.id eq id }
                    .map { Users.toUser(it) }.singleOrNull()
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(mapOf("error" to "User not found"))
                return@put
            }
            val uploadUser = try {
                call.receive<UpdateUserPayload>()
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid field."))
                return@put
            }
            uploadUser.username
                ?.takeIf { !it.equals(user.username, true) }
                ?.let {
                    if (!User.isUsernameValid(it)) {
                        call.response.status(HttpStatusCode.BadRequest)
                        call.respond(mapOf("error" to "Invalid Username."))
                        return@put
                    }
                    Database.dbQuery {
                        Users
                            .select { Users.username eq it }
                            .singleOrNull()
                    }?.let {
                        call.response.status(HttpStatusCode.BadRequest)
                        call.respond(mapOf("error" to "Username already used."))
                        return@put
                    }
                }
            Database.dbQuery {
                Users
                    .update({ Users.id eq user.id }) {
                        it[firstName] =
                            uploadUser.firstName ?: user.firstName
                        it[lastName] =
                            uploadUser.lastName ?: user.lastName
                        it[username] =
                            uploadUser.username ?: user.username
                        it[password] =
                            uploadUser.password?.let {
                                BCrypt.withDefaults()
                                    .hashToString(12, uploadUser.password?.toCharArray())
                            }
                                ?: user.password
                        it[picture] = uploadUser.picture ?: user.picture
                        if (user.role.hasPermission(Permission.USER_UPDATE)) {
                            it[role] =
                                (uploadUser.role ?: user.role).toString()
                        }
                    }
            }
            val newUser = Database.dbQuery {
                Users.select { Users.id eq id }
                    .map { Users.toUser(it) }.singleOrNull()
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(mapOf("error" to "User not found"))
                return@put
            }
            call.respond(newUser)
        }
        get("/{id}/posts") {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@get
            }
            val id = call.parameters["id"]?.toIntOrNull() ?: run {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid user id"))
                return@get
            }
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0L
            val posts = Database.dbQuery {
                Posts
                    .join(Users, JoinType.INNER)
                    .join(Favorites, JoinType.LEFT) {
                        Favorites.post_id eq Posts.id and (Favorites.user_id eq null or (Favorites.user_id eq user.id))
                    }
                    .select { Posts.user_id eq id }
                    .orderBy(Posts.date, SortOrder.DESC)
                    .limit(limit, offset)
                    .map(Posts::toPost)
            }
            call.respond(posts)
        }
        get("/{id}/followers") {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@get
            }
            val id = call.parameters["id"]?.toIntOrNull() ?: run {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid user id"))
                return@get
            }
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0L
            val follows = Database.dbQuery {
                Users
                    .join(Follows, JoinType.INNER, Follows.follower_id, Users.id)
                    .select { Follows.user_id eq id }
                    .limit(limit, offset)
                    .map(Users::toUser)
            }
            call.respond(follows)
        }
        get("/{id}/following") {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@get
            }
            val id = call.parameters["id"]?.toIntOrNull() ?: run {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid user id"))
                return@get
            }
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0L
            val follows = Database.dbQuery {
                Users
                    .join(Follows, JoinType.INNER, Follows.user_id, Users.id)
                    .select { Follows.follower_id eq id }
                    .limit(limit, offset)
                    .map(Users::toUser)
            }
            call.respond(follows)
        }
        post("/{id}/follow") {
            val id = call.parameters["id"]?.toIntOrNull() ?: run {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid user id"))
                return@post
            }
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@post
            }
            val follow = Database.dbQuery {
                Follows
                    .select { Follows.user_id eq id and (Follows.follower_id eq user.id) }
                    .map(Follows::toFollow)
                    .singleOrNull() ?: Follows.insert {
                    it[Follows.user_id] = id
                    it[Follows.follower_id] = user.id
                }.resultedValues?.map(Follows::toFollow)?.singleOrNull()
            } ?: run {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(mapOf("error" to "Error while creating post."))
                return@post
            }
            Database.dbQuery {
                Notifications
                    .insert {
                        it[Notifications.dest_id] = id
                        it[Notifications.type] = TypeStatus.FOLLOWER.toString()
                        it[Notifications.origin_id] = user.id
                        it[date] = Clock.System.now().toEpochMilliseconds()
                    }
            }
            NotificationsPlugin.sendNotificationToUser(
                id,
                NotificationData(
                    title = "New follower",
                    body_loc_key = "notifications_follow",
                    body_loc_args = listOf(user.username),
                )
            )
            call.response.status(HttpStatusCode.Created)
            call.respond(follow)
        }
        delete("/{id}/follow") {
            val id = call.parameters["id"]?.toIntOrNull() ?: run {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid user id"))
                return@delete
            }
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@delete
            }
            Database.dbQuery {
                Follows.deleteWhere {
                    Op.build { Follows.user_id eq id and (Follows.follower_id eq user.id) }
                }
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
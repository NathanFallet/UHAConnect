package me.nathanfallet.uhaconnect.api

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import me.nathanfallet.uhaconnect.models.UpdateUserPayload
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

fun Route.apiUsers() {
    route("/users") {
        get {
            val users = me.nathanfallet.uhaconnect.database.Database.dbQuery {
                me.nathanfallet.uhaconnect.models.Users.selectAll()
                    .map { me.nathanfallet.uhaconnect.models.Users.toUser(it) }
            }
            call.respond(users)
        }

        get("/me") {
            val user = getUser() ?: run {
                call.response.status(io.ktor.http.HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@get
            }
            call.respond(user)
        }

        put("/me") {
            val user = getUser() ?: run {
                call.response.status(io.ktor.http.HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "User not found"))
                return@put
            }
            val uploadUser = try {
                call.receive<UpdateUserPayload>()
            } catch (e: Exception) {
                call.response.status(io.ktor.http.HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid field."))
                return@put
            }

            uploadUser.username
                ?.takeIf { !it.equals(user.username, true) }
                ?.let {
                    if (!me.nathanfallet.uhaconnect.models.User.isUsernameValid(it)) {
                        call.response.status(io.ktor.http.HttpStatusCode.BadRequest)
                        call.respond(mapOf("error" to "Invalid Username."))
                        return@put
                    }
                    me.nathanfallet.uhaconnect.database.Database.dbQuery {
                        me.nathanfallet.uhaconnect.models.Users
                            .select { me.nathanfallet.uhaconnect.models.Users.username eq it }
                            .singleOrNull()
                    }?.let {
                        call.response.status(io.ktor.http.HttpStatusCode.BadRequest)
                        call.respond(mapOf("error" to "Username already used."))
                        return@put
                    }
                }

            me.nathanfallet.uhaconnect.database.Database.dbQuery {
                me.nathanfallet.uhaconnect.models.Users
                    .update({ me.nathanfallet.uhaconnect.models.Users.id eq user.id }) {
                        it[me.nathanfallet.uhaconnect.models.Users.firstName] =
                            uploadUser.firstName ?: user.firstName
                        it[me.nathanfallet.uhaconnect.models.Users.lastName] =
                            uploadUser.lastName ?: user.lastName
                        it[me.nathanfallet.uhaconnect.models.Users.username] =
                            uploadUser.username ?: user.username
                        it[me.nathanfallet.uhaconnect.models.Users.password] =
                            uploadUser.password?.let {
                                at.favre.lib.crypto.bcrypt.BCrypt.withDefaults()
                                    .hashToString(12, uploadUser.password?.toCharArray())
                            }
                                ?: user.password
                    }
            }

            val newUser = getUser() ?: run {
                call.response.status(io.ktor.http.HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@put
            }
            call.respond(newUser)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: run {
                call.response.status(io.ktor.http.HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid user id"))
                return@get
            }
            val user = me.nathanfallet.uhaconnect.database.Database.dbQuery {
                me.nathanfallet.uhaconnect.models.Users.select { me.nathanfallet.uhaconnect.models.Users.id eq id }
                    .map { me.nathanfallet.uhaconnect.models.Users.toUser(it) }.singleOrNull()
            } ?: run {
                call.response.status(io.ktor.http.HttpStatusCode.NotFound)
                call.respond(mapOf("error" to "User not found"))
                return@get
            }
            call.respond(user)
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: run {
                call.response.status(io.ktor.http.HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid user id"))
                return@put
            }
            val user = me.nathanfallet.uhaconnect.database.Database.dbQuery {
                me.nathanfallet.uhaconnect.models.Users.select { me.nathanfallet.uhaconnect.models.Users.id eq id }
                    .map { me.nathanfallet.uhaconnect.models.Users.toUser(it) }.singleOrNull()
            } ?: run {
                call.response.status(io.ktor.http.HttpStatusCode.NotFound)
                call.respond(mapOf("error" to "User not found"))
                return@put
            }
            val uploadUser = try {
                call.receive<UpdateUserPayload>()
            } catch (e: Exception) {
                call.response.status(io.ktor.http.HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid field."))
                return@put
            }

            uploadUser.username
                ?.takeIf { !it.equals(user.username, true) }
                ?.let {
                    if (!me.nathanfallet.uhaconnect.models.User.isUsernameValid(it)) {
                        call.response.status(io.ktor.http.HttpStatusCode.BadRequest)
                        call.respond(mapOf("error" to "Invalid Username."))
                        return@put
                    }
                    me.nathanfallet.uhaconnect.database.Database.dbQuery {
                        me.nathanfallet.uhaconnect.models.Users
                            .select { me.nathanfallet.uhaconnect.models.Users.username eq it }
                            .singleOrNull()
                    }?.let {
                        call.response.status(io.ktor.http.HttpStatusCode.BadRequest)
                        call.respond(mapOf("error" to "Username already used."))
                        return@put
                    }
                }

            me.nathanfallet.uhaconnect.database.Database.dbQuery {
                me.nathanfallet.uhaconnect.models.Users
                    .update({ me.nathanfallet.uhaconnect.models.Users.id eq user.id }) {
                        it[me.nathanfallet.uhaconnect.models.Users.firstName] =
                            uploadUser.firstName ?: user.firstName
                        it[me.nathanfallet.uhaconnect.models.Users.lastName] =
                            uploadUser.lastName ?: user.lastName
                        it[me.nathanfallet.uhaconnect.models.Users.username] =
                            uploadUser.username ?: user.username
                        it[me.nathanfallet.uhaconnect.models.Users.password] =
                            uploadUser.password?.let {
                                at.favre.lib.crypto.bcrypt.BCrypt.withDefaults()
                                    .hashToString(12, uploadUser.password?.toCharArray())
                            }
                                ?: user.password
                        if (user.role.hasPermission(me.nathanfallet.uhaconnect.models.Permission.USER_UPDATE)) {
                            it[me.nathanfallet.uhaconnect.models.Users.role] =
                                (uploadUser.role ?: user.role).toString()
                        }
                    }
            }

            val newUser = me.nathanfallet.uhaconnect.database.Database.dbQuery {
                me.nathanfallet.uhaconnect.models.Users.select { me.nathanfallet.uhaconnect.models.Users.id eq id }
                    .map { me.nathanfallet.uhaconnect.models.Users.toUser(it) }.singleOrNull()
            } ?: run {
                call.response.status(io.ktor.http.HttpStatusCode.NotFound)
                call.respond(mapOf("error" to "User not found"))
                return@put
            }

            call.respond(newUser)
        }

        get("/{id}/posts") {
            val id = call.parameters["id"]?.toIntOrNull() ?: run {
                call.response.status(io.ktor.http.HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid user id"))
                return@get
            }
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0L
            val posts = me.nathanfallet.uhaconnect.database.Database.dbQuery {
                me.nathanfallet.uhaconnect.models.Posts
                    .join(
                        me.nathanfallet.uhaconnect.models.Users,
                        org.jetbrains.exposed.sql.JoinType.INNER
                    )
                    .join(
                        me.nathanfallet.uhaconnect.models.Favorites,
                        org.jetbrains.exposed.sql.JoinType.LEFT,
                        me.nathanfallet.uhaconnect.models.Favorites.post_id,
                        me.nathanfallet.uhaconnect.models.Posts.id
                    )
                    .select { me.nathanfallet.uhaconnect.models.Posts.user_id eq id }
                    .orderBy(
                        me.nathanfallet.uhaconnect.models.Posts.date,
                        org.jetbrains.exposed.sql.SortOrder.DESC
                    )
                    .limit(limit, offset)
                    .map(me.nathanfallet.uhaconnect.models.Posts::toPost)
            }
            call.respond(posts)
        }
    }
}
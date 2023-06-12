package me.nathanfallet.uhaconnect.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import me.nathanfallet.uhaconnect.database.Database
import me.nathanfallet.uhaconnect.models.Favorites
import me.nathanfallet.uhaconnect.models.Posts
import me.nathanfallet.uhaconnect.models.Users
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
            val id = call.parameters["id"]?.toIntOrNull() ?: run {
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
                    .select { Favorites.post_id eq id and (Favorites.user_id eq user.id) }
                    .map(Favorites::toFavorite)
                    .singleOrNull() ?: Favorites.insert {
                    it[Favorites.user_id] = user.id
                    it[Favorites.post_id] = id
                }.resultedValues?.map(Favorites::toFavorite)?.singleOrNull()
            } ?: run {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(mapOf("error" to "Error while adding post to favorite."))
                return@post
            }
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
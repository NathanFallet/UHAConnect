package me.nathanfallet.uhaconnect.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.util.pipeline.PipelineContext
import me.nathanfallet.uhaconnect.database.Database
import me.nathanfallet.uhaconnect.models.Users
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

fun Route.api() {
    route("/auth") {
        post("/login") {
            // TODO: Login
        }
        post("/register") {
            // TODO: Register
        }
    }
    authenticate("api-jwt") {
        route("/users") {
            get {
                val users = Database.dbQuery {
                    Users.selectAll().map { Users.toUser(it) }
                }
                call.respond(users)
            }
            get("/me") {
                val user = getUser() ?: run {
                    call.response.status(HttpStatusCode.Unauthorized)
                    call.respond(mapOf("error" to "Invalid user"))
                    return@get
                }
                call.respond(User(1))
            }
            put("/me") {
                // TODO: Update my profile
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: run {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid user id"))
                    return@get
                }
                val user = Database.dbQuery {
                    Users.select { Users.id eq id }.map { Users.toUser(it) }.singleOrNull()
                } ?: run {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(mapOf("error" to "User not found"))
                    return@get
                }
                call.respond(user)
            }
        }
        route("/posts") {
            get {
                // TODO: Get posts, paginated
            }
            post {
                // TODO: Create a post
            }
            get("/{id}") {
                // TODO: Get a post by id
            }
            put("/{id}") {
                // TODO: Update a post by id, checking author (or admin rights)
            }
        }
        // ...
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.getUser(): User? {
    return call.principal<JWTPrincipal>()?.payload?.subject?.let { userId ->
        Database.dbQuery {
            Users.select { Users.id eq userId.toInt() }.map {
                Users.toUser(it)
            }.singleOrNull()
        }
    }
}

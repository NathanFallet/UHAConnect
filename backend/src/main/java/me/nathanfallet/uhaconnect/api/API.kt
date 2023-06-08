package me.nathanfallet.uhaconnect.api

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.util.pipeline.PipelineContext
import kotlinx.datetime.Clock
import me.nathanfallet.uhaconnect.database.Database
import me.nathanfallet.uhaconnect.models.CreatePostPayload
import me.nathanfallet.uhaconnect.models.LoginPayload
import me.nathanfallet.uhaconnect.models.Posts
import me.nathanfallet.uhaconnect.models.RegisterPayload
import me.nathanfallet.uhaconnect.models.RoleStatus
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.models.UserToken
import me.nathanfallet.uhaconnect.models.Users
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.util.Date

fun Route.api() {
    val secret = this.environment!!.config.property("jwt.secret").getString()
    val issuer = this.environment!!.config.property("jwt.issuer").getString()
    val audience = this.environment!!.config.property("jwt.audience").getString()
    val expiration = 365 * 24 * 60 * 60 * 1000L // 1 year

    route("/auth") {
        post("/login") {
            val connect = try {
                call.receive<LoginPayload>()
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid user"))
                return@post
            }

            val user = Database.dbQuery {
                Users
                    .select { (Users.username eq connect.username) or (Users.email eq connect.username) }
                    .map(Users::toUser)
                    .singleOrNull()
            }
                ?.takeIf {
                    BCrypt.verifyer().verify(connect.password.toCharArray(), it.password).verified
                }
                ?: run {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid user or password."))
                    return@post
                }
            val token = JWT.create()
                .withSubject(user.id.toString())
                .withAudience(audience)
                .withIssuer(issuer)
                .withExpiresAt(Date(System.currentTimeMillis() + expiration))
                .sign(Algorithm.HMAC256(secret))
            call.respond(UserToken(user, token))
        }

        post("/register") {
            val payload = try {
                call.receive<RegisterPayload>()
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid field."))
                return@post
            }

            Database.dbQuery {
                Users
                    .select { Users.email eq payload.email }
                    .singleOrNull()
            }?.let {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Email already used."))
                return@post
            }
            val newUser = Database.dbQuery {
                Users.insert {
                    it[Users.firstName] = payload.firstName
                    it[Users.lastName] = payload.lastName
                    it[Users.username] = payload.username
                    it[Users.email] = payload.email
                    it[Users.role] = RoleStatus.STUDENT.toString()
                    it[Users.password] = BCrypt.withDefaults().hashToString(12, payload.password.toCharArray())

                }.resultedValues?.map(Users::toUser)?.singleOrNull()
            } ?: run {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(mapOf("error" to "Error while creating user."))
                return@post
            }
            val token = JWT.create()
                .withSubject(newUser.id.toString())
                .withAudience(audience)
                .withIssuer(issuer)
                .withExpiresAt(Date(System.currentTimeMillis() + expiration))
                .sign(Algorithm.HMAC256(secret))
            call.respond(UserToken(newUser, token))
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
                call.respond(user)
            }
            put("/me") {
                // TODO: Update my profile
                val user = getUser() ?: run {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(mapOf("error" to "User not found"))
                    return@put
                }
                val uploadUser = try {
                    call.receive<RegisterPayload>()
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid field."))
                    return@put
                }
                Database.dbQuery {
                    Users
                        .update({ Users.id eq user.id }) {
                            it[Users.firstName] = uploadUser.firstName ?: user.firstName!!
                            it[Users.lastName] = uploadUser.lastName ?: user.lastName!!
                            it[Users.username] = uploadUser.username ?: user.lastName!!
                            it[Users.password] = BCrypt.withDefaults().hashToString(12, uploadUser.password.toCharArray()) ?: user.password!!
                        }
                }
                val newUser = getUser() ?: run {
                    call.response.status(HttpStatusCode.Unauthorized)
                    call.respond(mapOf("error" to "Invalid user"))
                    return@put
                }
                call.respond(newUser)

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
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0L
                val posts = Database.dbQuery {
                    Posts
                        .join(Users, JoinType.INNER)
                        .selectAll()
                        .orderBy(Posts.date, SortOrder.DESC)
                        .limit(limit, offset)
                        .map(Posts::toPost)

                }
                call.respond(posts)
            }
            post {
                val user = getUser() ?: run {
                    call.response.status(HttpStatusCode.Unauthorized)
                    call.respond(mapOf("error" to "Invalid user"))
                    return@post
                }
                val newPost = try {
                    call.receive<CreatePostPayload>()
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid title or content."))
                    return@post
                }

                val post = Database.dbQuery {
                    Posts.insert {
                        it[Posts.user_id] = user.id
                        it[Posts.title] = newPost.title
                        it[Posts.content] = newPost.content
                        it[Posts.date] = Clock.System.now().toEpochMilliseconds()
                    }.resultedValues?.map(Posts::toPost)?.singleOrNull()
                } ?: run {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(mapOf("error" to "Error while creating post."))
                    return@post
                }
                call.respond(post)
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: run {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid post id"))
                    return@get
                }
                val post = Database.dbQuery {
                    Posts
                        .select {Posts.id eq id}
                        .map(Posts::toPost)
                        .singleOrNull()
                } ?: run {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(mapOf("error" to "Post not found."))
                    return@get
                }
                call.respond(post)
            }
            put("/{id}") {
                // TODO: Update a post by id, checking author (or admin rights)
                val id = call.parameters["id"]?.toIntOrNull() ?: run {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid post id"))
                    return@put
                }
                val user = getUser() ?: run {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(mapOf("error" to "User not found"))
                    return@put
                }
                val post = Database.dbQuery {
                    Posts
                        .select {Posts.id eq id}
                        .map(Posts::toPost)
                        .singleOrNull()
                } ?: run {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(mapOf("error" to "Post not found."))
                    return@put
                }
                if (!(post.user_id == user.id || listOf(RoleStatus.ADMINISTRATOR, RoleStatus.MODERATOR).contains(user.role))) {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(mapOf("error" to "Cannot update this post."))
                    return@put
                }
                val uploadPost = try {
                    call.receive<CreatePostPayload>()
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid title or content."))
                    return@put
                }
                Database.dbQuery {
                    Posts
                        .update({ Posts.id eq id }) {
                            it[Posts.title] = uploadPost.title
                            it[Posts.content] = uploadPost.content
                        }
                    post.title = uploadPost.title
                    post.content = uploadPost.content
                }
                call.respond(post)
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

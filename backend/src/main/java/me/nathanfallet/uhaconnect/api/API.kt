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
import io.ktor.server.http.content.staticFiles
import io.ktor.server.request.receive
import io.ktor.server.request.receiveStream
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import me.nathanfallet.uhaconnect.database.Database
import me.nathanfallet.uhaconnect.models.Comments
import me.nathanfallet.uhaconnect.models.CreateCommentPayload
import me.nathanfallet.uhaconnect.models.CreatePostPayload
import me.nathanfallet.uhaconnect.models.Favorites
import me.nathanfallet.uhaconnect.models.LoginPayload
import me.nathanfallet.uhaconnect.models.Notifications
import me.nathanfallet.uhaconnect.models.NotificationsTokenPayload
import me.nathanfallet.uhaconnect.models.NotificationsTokens
import me.nathanfallet.uhaconnect.models.Permission
import me.nathanfallet.uhaconnect.models.Posts
import me.nathanfallet.uhaconnect.models.RegisterPayload
import me.nathanfallet.uhaconnect.models.UpdatePostPayload
import me.nathanfallet.uhaconnect.models.UpdateUserPayload
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.models.UserToken
import me.nathanfallet.uhaconnect.models.Users
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
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Date
import java.util.UUID


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
            if (!User.isEmailValid(payload.email)) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid Email."))
            }
            if (!User.isUsernameValid(payload.username)) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid Username."))
            }
            Database.dbQuery {
                Users
                    .select { Users.email eq payload.email or (Users.username eq payload.username) }
                    .singleOrNull()
            }?.let {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Email or username already used."))
                return@post
            }
            val newUser = Database.dbQuery {
                Users.insert {
                    it[Users.firstName] = payload.firstName
                    it[Users.lastName] = payload.lastName
                    it[Users.username] = payload.username
                    it[Users.email] = payload.email
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
                val user = getUser() ?: run {
                    call.response.status(HttpStatusCode.Unauthorized)
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
                            it[Users.firstName] = uploadUser.firstName ?: user.firstName
                            it[Users.lastName] = uploadUser.lastName ?: user.lastName
                            it[Users.username] = uploadUser.username ?: user.username
                            it[Users.password] = uploadUser.password?.let {
                                BCrypt.withDefaults()
                                    .hashToString(12, uploadUser.password?.toCharArray())
                            }
                                ?: user.password
                            if (user.role.hasPermission(Permission.USER_UPDATE)) {
                                it[Users.role] = (uploadUser.role ?: user.role).toString()
                            }
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

            get("/{id}/posts") {
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
                        .select { Posts.user_id eq id }
                        .orderBy(Posts.date, SortOrder.DESC)
                        .limit(limit, offset)
                        .map(Posts::toPost)
                }
                call.respond(posts)
            }
        }

        route("/posts") {
            get {
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0L
                val posts = Database.dbQuery {
                    Posts
                        .join(Users, JoinType.INNER)
                        .select { Posts.validated eq true }
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
                call.response.status(HttpStatusCode.Created)
                call.respond(post)
            }
            get("/requests") {
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0L
                val posts = Database.dbQuery {
                    Posts
                        .join(Users, JoinType.INNER)
                        .select { Posts.validated eq false }
                        .orderBy(Posts.date, SortOrder.DESC)
                        .limit(limit, offset)
                        .map(Posts::toPost)
                }
                call.respond(posts)
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: run {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid post id"))
                    return@get
                }
                val post = Database.dbQuery {
                    Posts
                        .select { Posts.id eq id }
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
                val id = call.parameters["id"]?.toIntOrNull() ?: run {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid post id"))
                    return@put
                }
                val user = getUser() ?: run {
                    call.response.status(HttpStatusCode.Unauthorized)
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
                if (!(post.user_id == user.id || user.role.hasPermission(Permission.POST_UPDATE))) {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(mapOf("error" to "Cannot update this post."))
                    return@put
                }
                val uploadPost = try {
                    call.receive<UpdatePostPayload>()
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid title or content."))
                    return@put
                }
                Database.dbQuery {
                    Posts
                        .update({ Posts.id eq id }) {
                            it[Posts.title] = uploadPost.title ?: post.title
                            it[Posts.content] = uploadPost.content ?: post.content
                            uploadPost.validated
                                ?.takeIf { user.role.hasPermission(Permission.POST_UPDATE) }
                                ?.let { validated ->
                                    it[Posts.validated] = validated
                                }
                        }
                }
                val newPost = Database.dbQuery {
                    Posts
                        .select { Posts.id eq id }
                        .map(Posts::toPost)
                        .singleOrNull()
                } ?: run {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(mapOf("error" to "Post not found."))
                    return@put
                }
                call.respond(newPost)
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
                val post = Database.dbQuery {
                    Posts
                        .select { Posts.id eq id }
                        .map(Posts::toPost)
                        .singleOrNull()
                } ?: run {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(mapOf("error" to "Post not found."))
                    return@delete
                }
                if (!(post.user_id == user.id || user.role.hasPermission(Permission.POST_DELETE))) {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(mapOf("error" to "Cannot delete this post."))
                    return@delete
                }
                Database.dbQuery {
                    Favorites.deleteWhere {
                        Op.build { Favorites.post_id eq id }
                    }
                    Comments.deleteWhere {
                        Op.build { Comments.post_id eq id }
                    }
                    Posts.deleteWhere {
                        Op.build { Posts.id eq id }
                    }
                }
                call.respond(HttpStatusCode.NoContent)
            }

            get("/{id}/comments") {
                val id = call.parameters["id"]?.toIntOrNull() ?: run {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid post id"))
                    return@get
                }
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0L
                val comments = Database.dbQuery {
                    Comments
                        .select { Comments.post_id eq id }
                        .orderBy(Comments.date, SortOrder.DESC)
                        .limit(limit, offset)
                        .map(Comments::toComment)
                }
                call.respond(comments)
            }
            post("/{id}/comments") {
                val id = call.parameters["id"]?.toIntOrNull() ?: run {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid post id"))
                    return@post
                }
                val post =
                    Database.dbQuery {
                        Posts
                            .select { Posts.id eq id }
                            .map(Posts::toPost)
                            .singleOrNull()
                    } ?: run {
                        call.response.status(HttpStatusCode.InternalServerError)
                        call.respond(mapOf("error" to "Post not found."))
                        return@post
                    }
                val newComment = try {
                    call.receive<CreateCommentPayload>()
                } catch (e: Exception) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid comment."))
                    return@post
                }

                val comment = Database.dbQuery {
                    Comments.insert {
                        it[Comments.post_id] = post.id
                        it[Comments.user_id] = post.user_id
                        it[Comments.content] = newComment.content
                        it[Comments.date] = Clock.System.now().toEpochMilliseconds()
                    }.resultedValues?.map(Comments::toComment)?.singleOrNull()
                } ?: run {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(mapOf("error" to "Error while creating post."))
                    return@post
                }
                call.response.status(HttpStatusCode.Created)
                call.respond(comment)
            }
            delete("/{id}/comments/{comment_id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: run {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid post id"))
                    return@delete
                }
                val comment_id = call.parameters["comment_id"]?.toIntOrNull() ?: run {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid comment id"))
                    return@delete
                }
                val user = getUser() ?: run {
                    call.response.status(HttpStatusCode.Unauthorized)
                    call.respond(mapOf("error" to "User not found"))
                    return@delete
                }
                val post = Database.dbQuery {
                    Posts
                        .select { Posts.id eq id }
                        .map(Posts::toPost)
                        .singleOrNull()
                } ?: run {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(mapOf("error" to "Post not found."))
                    return@delete
                }
                val comment = Database.dbQuery {
                    Comments
                        .select { Comments.id eq comment_id }
                        .map(Comments::toComment)
                        .singleOrNull()
                } ?: run {
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(mapOf("error" to "Comment not found."))
                    return@delete
                }
                if (!(post.user_id == user.id || user.role.hasPermission(Permission.COMMENT_DELETE))) {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(mapOf("error" to "Cannot delete this comment."))
                    return@delete
                }
                Database.dbQuery {
                    Comments.deleteWhere {
                        Op.build { Comments.post_id eq id }
                    }
                }
                call.respond(HttpStatusCode.NoContent)
            }

        }

        route("/media") {
            post {
                val user = getUser() ?: run {
                    call.response.status(HttpStatusCode.Unauthorized)
                    call.respond(mapOf("error" to "Invalid user"))
                    return@post
                }

                val uploadsFolder = Paths.get("media")
                if (!Files.exists(uploadsFolder)) {
                    Files.createDirectory(uploadsFolder)
                }

                call.receiveStream().use { input ->
                    val fileName = generateRandomName()
                    val file = File("media/$fileName")
                    withContext(Dispatchers.IO) {
                        file.outputStream().buffered().use {
                            input.copyTo(it)
                        }
                    }
                    call.respond(mapOf("fileName" to fileName)) // Include fileName in the response
                }

                call.response.status(HttpStatusCode.Created)
            }

            staticFiles("", File("media"))

            // Add more media-related routes as needed
        }


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
                        .join(Users, JoinType.INNER, Notifications.dest_id, Users.id)
                        .select { Notifications.dest_id eq user.id }
                        .orderBy(Notifications.date, SortOrder.DESC)
                        .limit(limit, offset)
                        .map(Posts::toPost)
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
                        .join(Favorites, JoinType.INNER)
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
                        .select { Favorites.post_id eq id }
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

        // ...
    }
}

fun generateRandomName(): String {
    val uuid = UUID.randomUUID()
    return uuid.toString().substring(0, 8)
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

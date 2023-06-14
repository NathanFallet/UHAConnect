package me.nathanfallet.uhaconnect.api

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
import me.nathanfallet.uhaconnect.models.Comments
import me.nathanfallet.uhaconnect.models.CreateCommentPayload
import me.nathanfallet.uhaconnect.models.CreatePostPayload
import me.nathanfallet.uhaconnect.models.Favorites
import me.nathanfallet.uhaconnect.models.Notifications
import me.nathanfallet.uhaconnect.models.Permission
import me.nathanfallet.uhaconnect.models.Post
import me.nathanfallet.uhaconnect.models.Posts
import me.nathanfallet.uhaconnect.models.RoleStatus
import me.nathanfallet.uhaconnect.models.TypeStatus
import me.nathanfallet.uhaconnect.models.UpdatePostPayload
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
import org.jetbrains.exposed.sql.update

fun Route.apiPosts() {
    route("/posts") {
        get {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@get
            }
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0L
            val posts = Database.dbQuery {
                Posts
                    .join(Users, JoinType.INNER, Posts.user_id, Users.id)
                    .join(Favorites, JoinType.LEFT) {
                        Favorites.post_id eq Posts.id and (Favorites.user_id eq null or (Favorites.user_id eq user.id))
                    }
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
            if (user.role == RoleStatus.BANNED) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(mapOf("error" to "User unauthorized."))
                return@post
            }
            val newPost = try {
                call.receive<CreatePostPayload>()
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid title or content."))
                return@post
            }
            if (!Post.isTitleValid(newPost.title)) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Title is blank or too long."))
                return@post
            }
            val post = Database.dbQuery {
                Posts.insert {
                    it[user_id] = user.id
                    it[title] = newPost.title
                    it[content] = newPost.content
                    it[date] = Clock.System.now().toEpochMilliseconds()
                }.resultedValues?.map(Posts::toPost)?.singleOrNull()
            } ?: run {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(mapOf("error" to "Error while creating post."))
                return@post
            }
            NotificationsPlugin.sendNotificationToFollowers(
                user.id,
                NotificationData(
                    title_loc_key = "notifications_post",
                    title_loc_args = listOf(user.username),
                    body = post.title,
                )
            )
            call.response.status(HttpStatusCode.Created)
            call.respond(post)
        }
        get("/requests") {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@get
            }
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0L
            val posts = Database.dbQuery {
                Posts
                    .join(Users, JoinType.INNER, Posts.user_id, Users.id)
                    .join(Favorites, JoinType.LEFT) {
                        Favorites.post_id eq Posts.id and (Favorites.user_id eq null or (Favorites.user_id eq user.id))
                    }
                    .select { Posts.validated eq false }
                    .orderBy(Posts.date, SortOrder.DESC)
                    .limit(limit, offset)
                    .map(Posts::toPost)
            }
            call.respond(posts)
        }
        get("/{id}") {
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@get
            }
            val id = call.parameters["id"]?.toIntOrNull() ?: run {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid post id"))
                return@get
            }
            val post = Database.dbQuery {
                Posts
                    .join(Users, JoinType.INNER, Posts.user_id, Users.id)
                    .join(Favorites, JoinType.LEFT) {
                        Favorites.post_id eq Posts.id and (Favorites.user_id eq null or (Favorites.user_id eq user.id))
                    }
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
                    .select { Posts.id eq id }
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
                        it[title] = uploadPost.title ?: post.title
                        it[content] = uploadPost.content ?: post.content
                        uploadPost.validated
                            ?.takeIf { user.role.hasPermission(Permission.POST_UPDATE) }
                            ?.let { validated ->
                                it[Posts.validated] = validated
                            }
                        if (post.user_id == user.id && !(user.role.hasPermission(Permission.POST_UPDATE))) {
                            it[validated] = false
                        }
                    }
            }
            val newPost = Database.dbQuery {
                Posts
                    .join(Users, JoinType.INNER, Posts.user_id, Users.id)
                    .join(Favorites, JoinType.LEFT, Favorites.post_id, Posts.id)
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
                    .join(Users, JoinType.INNER)
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
                call.respond(mapOf("error" to "Invalid post id."))
                return@post
            }
            val user = getUser() ?: run {
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "User not found."))
                return@post
            }
            if (user.role == RoleStatus.BANNED) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(mapOf("error" to "User unauthorized."))
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
                    it[post_id] = post.id
                    it[user_id] = user.id
                    it[content] = newComment.content
                    it[date] = Clock.System.now().toEpochMilliseconds()
                }.resultedValues?.map(Comments::toComment)?.singleOrNull()
            } ?: run {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(mapOf("error" to "Error while creating post."))
                return@post
            }
            Database.dbQuery {
                Notifications
                    .insert {
                        it[dest_id] = post.user_id
                        it[post_id] = post.id
                        it[type] = TypeStatus.COMMENT.toString()
                        it[origin_id] = user.id
                        it[date] = Clock.System.now().toEpochMilliseconds()
                    }
            }
            NotificationsPlugin.sendNotificationToUser(
                post.user_id,
                NotificationData(
                    title_loc_key = "notifications_comment",
                    title_loc_args = listOf(user.username),
                    body = comment.content
                )
            )
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
            if (post.user_id != user.id && comment.user_id != user.id && !user.role.hasPermission(
                    Permission.COMMENT_DELETE
                )
            ) {
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(mapOf("error" to "Cannot delete this comment."))
                return@delete
            }
            Database.dbQuery {
                Comments.deleteWhere {
                    Op.build { Comments.id eq comment_id }
                }
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
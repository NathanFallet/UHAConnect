package me.nathanfallet.uhaconnect.api

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.routing.Route
import io.ktor.util.pipeline.PipelineContext
import me.nathanfallet.uhaconnect.database.Database
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.models.Users
import org.jetbrains.exposed.sql.select
import java.util.UUID

fun Route.api() {
    apiAuth()

    authenticate("api-jwt") {
        apiUsers()
        apiPosts()
        apiMedia()
        apiNotifications()
        apiFavorites()
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

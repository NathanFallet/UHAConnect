package me.nathanfallet.uhaconnect.api

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import me.nathanfallet.uhaconnect.database.Database
import me.nathanfallet.uhaconnect.models.Users
import org.jetbrains.exposed.sql.selectAll

fun Route.api() {
    route("/users") {
        get {
            val users = Database.dbQuery {
                Users.selectAll().map { Users.toUser(it) }
            }
            call.respond(users)
        }
        post {

        }
        get("/me") {
            //call.respond(User(1))
        }
    }
}

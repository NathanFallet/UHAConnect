package me.nathanfallet.uhaconnect.api

import io.ktor.server.auth.authenticate
import io.ktor.server.http.content.staticFiles
import io.ktor.server.routing.Route
import java.io.File

fun Route.api() {
    apiAuth()
    staticFiles("media", File("media"))
    authenticate("api-jwt") {
        apiUsers()
        apiPosts()
        apiMedia()
        apiNotifications()
        apiFavorites()
    }
}

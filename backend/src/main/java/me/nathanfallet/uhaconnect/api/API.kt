package me.nathanfallet.uhaconnect.api

import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route

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

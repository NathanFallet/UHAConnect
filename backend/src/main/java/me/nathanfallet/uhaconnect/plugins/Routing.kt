package me.nathanfallet.uhaconnect.plugins

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import me.nathanfallet.uhaconnect.api.api

fun Application.configureRouting() {
    routing {
        api()
    }
}

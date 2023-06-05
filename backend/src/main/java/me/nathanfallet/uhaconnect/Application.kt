package me.nathanfallet.uhaconnect

import io.ktor.server.application.Application
import me.nathanfallet.uhaconnect.database.Database
import me.nathanfallet.uhaconnect.plugins.configureRouting
import me.nathanfallet.uhaconnect.plugins.configureSerialization

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    Database.init(this)

    configureSerialization()
    configureRouting()
}

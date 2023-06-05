package me.nathanfallet.uhaconnect

import io.ktor.server.application.*
import me.nathanfallet.uhaconnect.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureRouting()
}

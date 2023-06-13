package me.nathanfallet.uhaconnect.api

import io.ktor.server.application.call
import io.ktor.server.http.content.staticFiles
import io.ktor.server.request.receiveStream
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.coroutines.withContext
import me.nathanfallet.uhaconnect.models.MediaPayload
import java.io.File

fun Route.apiMedia() {
    route("/media") {
        post {
            val user = getUser() ?: run {
                call.response.status(io.ktor.http.HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid user"))
                return@post
            }

            val uploadsFolder = java.nio.file.Paths.get("media")
            if (!java.nio.file.Files.exists(uploadsFolder)) {
                java.nio.file.Files.createDirectory(uploadsFolder)
            }

            call.receiveStream().use { input ->
                val fileName = generateRandomName()
                val file = File("media/$fileName")
                withContext(kotlinx.coroutines.Dispatchers.IO) {
                    file.outputStream().buffered().use {
                        input.copyTo(it)
                    }
                }
                call.respond(MediaPayload(fileName)) // Include fileName in the response
            }

            call.response.status(io.ktor.http.HttpStatusCode.Created)
        }

        staticFiles("", File("media"))

        // Add more media-related routes as needed
    }
}
package me.nathanfallet.uhaconnect.api

import io.ktor.server.application.call
import io.ktor.server.request.receiveStream
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.coroutines.withContext
import me.nathanfallet.uhaconnect.models.MediaPayload
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID

fun Route.apiMedia() {
    route("/media") {
        post {
            val uploadsFolder = Paths.get("media")
            if (!Files.exists(uploadsFolder)) {
                Files.createDirectory(uploadsFolder)
            }
            call.receiveStream().use { input ->
                val fileName = generateMediaName()
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
    }
}

fun generateMediaName(): String {
    val uuid = UUID.randomUUID().toString()
    return if (Files.exists(Paths.get("media/$uuid"))) {
        generateMediaName()
    } else {
        uuid
    }
}

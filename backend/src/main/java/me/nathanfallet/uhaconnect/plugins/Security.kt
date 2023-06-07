package me.nathanfallet.uhaconnect.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond

fun Application.configureSecurity() {
    authentication {
        jwt("api-jwt") {
            val secret =
                this@configureSecurity.environment.config.property("jwt.secret").getString()
            val issuer =
                this@configureSecurity.environment.config.property("jwt.issuer").getString()
            val audience =
                this@configureSecurity.environment.config.property("jwt.audience").getString()
            verifier(
                JWT.require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate {
                JWTPrincipal(it.payload)
            }
            challenge { _, _ ->
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(mapOf("error" to "Invalid token"))
            }
        }
    }
}

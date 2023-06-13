package me.nathanfallet.uhaconnect.api

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.pipeline.PipelineContext
import me.nathanfallet.uhaconnect.database.Database
import me.nathanfallet.uhaconnect.models.AuthResetCodes
import me.nathanfallet.uhaconnect.models.LoginPayload
import me.nathanfallet.uhaconnect.models.RegisterPayload
import me.nathanfallet.uhaconnect.models.ResetPasswordPayload
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.models.UserToken
import me.nathanfallet.uhaconnect.models.Users
import me.nathanfallet.uhaconnect.plugins.Emails
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.util.Date

fun Route.apiAuth() {
    val secret = this.environment!!.config.property("jwt.secret").getString()
    val issuer = this.environment!!.config.property("jwt.issuer").getString()
    val audience = this.environment!!.config.property("jwt.audience").getString()
    val expiration = 365 * 24 * 60 * 60 * 1000L // 1 year

    route("/auth") {
        post("/login") {
            val connect = try {
                call.receive<LoginPayload>()
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid user"))
                return@post
            }

            val user = Database.dbQuery {
                Users
                    .select { (Users.username eq connect.username) or (Users.email eq connect.username) }
                    .map(Users::toUser)
                    .singleOrNull()
            }
                ?.takeIf {
                    BCrypt.verifyer().verify(connect.password.toCharArray(), it.password).verified
                }
                ?: run {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Invalid user or password."))
                    return@post
                }
            val token = JWT.create()
                .withSubject(user.id.toString())
                .withAudience(audience)
                .withIssuer(issuer)
                .withExpiresAt(Date(System.currentTimeMillis() + expiration))
                .sign(Algorithm.HMAC256(secret))
            call.respond(UserToken(user, token))
        }
        post("/register") {
            val payload = try {
                call.receive<RegisterPayload>()
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid field."))
                return@post
            }
            if (!User.isEmailValid(payload.email)) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid Email."))
                return@post
            }
            if (!User.isUsernameValid(payload.username)) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid Username."))
                return@post
            }
            Database.dbQuery {
                Users
                    .select { Users.email eq payload.email or (Users.username eq payload.username) }
                    .singleOrNull()
            }?.let {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Email or username already used."))
                return@post
            }
            val newUser = Database.dbQuery {
                Users.insert {
                    it[Users.firstName] = payload.firstName
                    it[Users.lastName] = payload.lastName
                    it[Users.username] = payload.username
                    it[Users.email] = payload.email
                    it[Users.password] =
                        BCrypt.withDefaults().hashToString(12, payload.password.toCharArray())
                }.resultedValues?.map(Users::toUser)?.singleOrNull()
            } ?: run {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(mapOf("error" to "Error while creating user."))
                return@post
            }
            Emails.sendEmail(
                newUser.email,
                "Welcome to UHA Connect",
                "Welcome ${newUser.firstName} to UHA Connect!<br/>We're happy to see you here!<br/><br/>" +
                        "You can now login to your account using your email address or your username.<br/><br/>" +
                        "Have a nice day!<br/>UHA Connect team."
            )
            val token = JWT.create()
                .withSubject(newUser.id.toString())
                .withAudience(audience)
                .withIssuer(issuer)
                .withExpiresAt(Date(System.currentTimeMillis() + expiration))
                .sign(Algorithm.HMAC256(secret))
            call.respond(UserToken(newUser, token))
        }
        post("/reset") {
            val payload = try {
                call.receive<ResetPasswordPayload>()
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("error" to "Invalid payload."))
                return@post
            }
            payload.email?.let { email ->
                val user = Database.dbQuery {
                    Users
                        .select { Users.email eq email }
                        .map(Users::toUser)
                        .singleOrNull()
                } ?: run {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(mapOf("error" to "Email not found."))
                    return@post
                }
                val code = Database.dbQuery {
                    val value = AuthResetCodes.generateCode()
                    AuthResetCodes.insert {
                        it[AuthResetCodes.code] = value
                        it[AuthResetCodes.userId] = user.id
                        it[AuthResetCodes.expiration] =
                            System.currentTimeMillis() + 10 * 60 * 1000L // 10 minutes
                    }
                    value
                }
                Emails.sendEmail(
                    user.email,
                    "UHA Connect - Reset password",
                    "Use this code to reset your password: $code.<br/>It expires in 10 minutes."
                )
                call.respond(HttpStatusCode.Created)
                return@post
            }
            payload.code?.let { code ->
                val password = payload.password ?: run {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Password is required."))
                    return@post
                }
                val resetCode = Database.dbQuery {
                    AuthResetCodes
                        .select { AuthResetCodes.code eq code }
                        .map(AuthResetCodes::toCode)
                        .singleOrNull()
                } ?: run {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(mapOf("error" to "Code not found."))
                    return@post
                }
                if (resetCode.expiration < System.currentTimeMillis()) {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(mapOf("error" to "Code expired."))
                    return@post
                }
                val user = Database.dbQuery {
                    Users
                        .select { Users.id eq resetCode.userId }
                        .map(Users::toUser)
                        .singleOrNull()
                } ?: run {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(mapOf("error" to "User not found."))
                    return@post
                }
                val newPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())
                Database.dbQuery {
                    Users.update({ Users.id eq user.id }) {
                        it[Users.password] = newPassword
                    }
                    AuthResetCodes.deleteWhere { AuthResetCodes.code eq code }
                }
                call.respond(HttpStatusCode.OK)
                return@post
            }
        }
    }
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

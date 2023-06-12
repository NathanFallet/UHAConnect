package me.nathanfallet.uhaconnect.database

import io.ktor.server.application.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.Comments
import me.nathanfallet.uhaconnect.models.Favorites
import me.nathanfallet.uhaconnect.models.Follows
import me.nathanfallet.uhaconnect.models.Notifications
import me.nathanfallet.uhaconnect.models.NotificationsToken
import me.nathanfallet.uhaconnect.models.Posts
import me.nathanfallet.uhaconnect.models.Users
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

object Database {

    private lateinit var host: String
    private lateinit var name: String
    private lateinit var user: String
    private lateinit var password: String

    fun init(application: Application) {
        // Read configuration
        host = application.environment.config.property("database.host").getString()
        name = application.environment.config.property("database.name").getString()
        user = application.environment.config.property("database.user").getString()
        password = application.environment.config.property("database.password").getString()

        // Connect to database
        org.jetbrains.exposed.sql.Database.connect(
            "jdbc:mysql://$host:3306/$name",
            "com.mysql.cj.jdbc.Driver",
            user, password
        )

        // Create tables (if needed)
        transaction {
            SchemaUtils.create(Users)
            SchemaUtils.create(Follows)
            SchemaUtils.create(Posts)
            SchemaUtils.create(Favorites)
            SchemaUtils.create(Comments)
            SchemaUtils.create(Notifications)
            SchemaUtils.create(NotificationsToken)
        }

        // Launch expiration
        Timer().scheduleAtFixedRate(0, 60 * 60 * 1000L) {
            CoroutineScope(Job()).launch {
                doExpiration()
            }
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    private suspend fun doExpiration() {
        Database.dbQuery {

        }
    }

}

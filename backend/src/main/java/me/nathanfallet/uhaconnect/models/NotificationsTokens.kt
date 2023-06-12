package me.nathanfallet.uhaconnect.models

import org.jetbrains.exposed.sql.Table

object NotificationsTokens : Table() {

    val token = varchar("token", 255)
    val userId = reference("user_id", Users)
    val expiration = long("expiration")

    override val primaryKey = PrimaryKey(token)

}

package me.nathanfallet.uhaconnect.models

import org.jetbrains.exposed.sql.Table

object NotificationsTokens : Table() {

    val token = varchar("token", 255)
    val userId = reference("id_user", Users)
    val expiration = Posts.long("expiration")

    override val primaryKey = PrimaryKey(token)

}
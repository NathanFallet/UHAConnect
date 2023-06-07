package me.nathanfallet.uhaconnect.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Notifications : Table() {
    val id_user = reference("id_user", Users)
    val id_post = reference("id_post", Posts)
    val type = varchar("type", 6)
    val id_origin = reference("id_origin", Users)

    override val primaryKey = PrimaryKey(id_user, id_post, name = "PK_follows")

    fun toNotifications(row: ResultRow): Notification {
        return Notification(
            id_user = row[id_user].value,
            id_post = row[id_post].value,
            type = TypeStatus.valueOf(row[type]),
            id_origin = row[id_origin].value
        )
    }

}
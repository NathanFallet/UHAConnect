package me.nathanfallet.uhaconnect.models

import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Notifications : Table() {
    val user_id = reference("user_id", Users)
    val post_id = reference("post_id", Posts)
    val type = varchar("type", 6)
    val origin_id = reference("origin_id", Users)
    val date = long("date")

    override val primaryKey = PrimaryKey(user_id, post_id, name = "PK_follows")

    fun toNotifications(row: ResultRow): Notification {
        return Notification(
            user_id = row[user_id].value,
            post_id = row[post_id].value,
            type = TypeStatus.valueOf(row[type]),
            origin_id = row[origin_id].value,
            date = Instant.fromEpochMilliseconds(row[Comments.date])
        )
    }

}
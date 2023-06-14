package me.nathanfallet.uhaconnect.models

import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object Notifications : IntIdTable() {

    val dest_id = reference("dest_id", Users)
    val post_id = integer("post_id").nullable()
    val type = varchar("type", 64)
    val origin_id = reference("origin_id", Users)
    val date = long("date")

    fun toNotification(row: ResultRow): Notification {
        val user = if (row.hasValue(Users.id)) Users.toUser(row)
        else null
        return Notification(
            id = row[id].value,
            dest_id = row[dest_id].value,
            post_id = row.getOrNull(post_id),
            type = TypeStatus.valueOf(row[type]),
            origin_id = row[origin_id].value,
            date = Instant.fromEpochMilliseconds(row[date]),
            user = user
        )
    }

}
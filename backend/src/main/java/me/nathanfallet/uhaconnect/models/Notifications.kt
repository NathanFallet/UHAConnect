package me.nathanfallet.uhaconnect.models

import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object Notifications : IntIdTable() {
    val dest_id = reference("dest_id", Users)
    val post_id = reference("post_id", Posts)
    val type = varchar("type", 6)
    val origin_id = reference("origin_id", Users)
    val date = long("date")

    override val primaryKey = PrimaryKey(id)

    fun toNotifications(row: ResultRow): Notification {
        val user = if (row.hasValue(Users.id)) Users.toUser(row)
        else null
        return Notification(
            dest_id = row[dest_id].value,
            post_id = row[post_id].value,
            type = TypeStatus.valueOf(row[type]),
            origin_id = row[origin_id].value,
            date = Instant.fromEpochMilliseconds(row[Comments.date]),
            user = user
        )
    }

}
package me.nathanfallet.uhaconnect.models

import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object Comments : IntIdTable() {

    val post_id = reference("post_id", Posts)
    val user_id = reference("user_id", Users)
    val content = text("content")
    val date = long("date")

    fun toComment(row: ResultRow): Comment {
        val user = if (row.hasValue(Users.id)) Users.toUser(row)
        else null
        return Comment(
            id = row[id].value,
            post_id = row[post_id].value,
            user_id = row[user_id].value,
            content = row[content],
            date = Instant.fromEpochMilliseconds(row[date]),
            user = user
        )
    }

}
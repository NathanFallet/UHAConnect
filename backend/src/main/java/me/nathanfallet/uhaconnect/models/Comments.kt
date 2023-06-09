package me.nathanfallet.uhaconnect.models

import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object Comments : IntIdTable() {
    val post_id = reference("id_post", Posts)
    val user_id = reference("id_user", Users)
    val content = text("content")
    val date = long("date")

    override val primaryKey = PrimaryKey(id)

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
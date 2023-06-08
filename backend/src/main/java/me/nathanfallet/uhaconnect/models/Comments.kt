package me.nathanfallet.uhaconnect.models

import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Comments : Table() {
    val post_id = reference("id_post", Posts)
    val user_id = reference("id_user", Users)
    val content = text("content")
    val date = long("date")

    override val primaryKey = PrimaryKey(post_id, user_id, name = "PK_Comments")

    fun toComment(row: ResultRow): Comment {
        return Comment(
            post_id = row[post_id].value,
            user_id = row[user_id].value,
            content = row[content],
            date = Instant.fromEpochMilliseconds(row[date])
        )
    }

}
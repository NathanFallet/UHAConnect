package me.nathanfallet.uhaconnect.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Comments : Table() {
    val id_post = reference("id_post", Posts)
    val id_user = reference("id_user", Users)
    val content = text("content")

    override val primaryKey = PrimaryKey(id_post, id_user, name = "PK_Comments")

    fun toComments(row: ResultRow): Comment {
        return Comment(
            id_post = row[id_post].value,
            id_user = row[id_user].value,
            content = row[content]
        )
    }

}
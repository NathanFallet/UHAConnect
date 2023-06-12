package me.nathanfallet.uhaconnect.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Favorites : Table() {
    val user_id = reference("user_id", Users)
    val post_id = reference("id_post", Posts)

    override val primaryKey = PrimaryKey(user_id, post_id, name = "PK_favorites")

    fun toFavorite(row: ResultRow): Favorite {
        val post = if (row.hasValue(Posts.id)) Posts.toPost(row)
        else null
        return Favorite(
            user_id = row[user_id].value,
            post_id = row[post_id].value,
            post = post
        )
    }

}
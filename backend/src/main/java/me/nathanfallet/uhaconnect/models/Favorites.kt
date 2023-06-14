package me.nathanfallet.uhaconnect.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Favorites : Table() {

    val user_id = reference("user_id", Users)
    val post_id = reference("post_id", Posts)

    override val primaryKey = PrimaryKey(user_id, post_id, name = "PK_favorites")

    fun toFavorite(row: ResultRow): Favorite {
        return Favorite(
            user_id = row[user_id].value,
            post_id = row[post_id].value
        )
    }

}
package me.nathanfallet.uhaconnect.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Favorites : Table() {
    val id_user = reference("id_user", Users)
    val id_post = reference("id_post", Posts)

    override val primaryKey = PrimaryKey(id_user, id_post, name = "PK_favorites")

    fun toFavorites(row: ResultRow): Favorite {
        return Favorite(
            id_user = row[id_user].value,
            id_post = row[id_post].value
        )
    }

}
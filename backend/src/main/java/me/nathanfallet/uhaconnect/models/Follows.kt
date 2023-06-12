package me.nathanfallet.uhaconnect.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Follows : Table() {
    val id_user = reference("id_user", Users) // User that is followed
    val id_follower = reference("id_follower", Users) // User that follows

    override val primaryKey = PrimaryKey(id_user, id_follower, name = "PK_follows")

    fun toFollows(row: ResultRow): Follow {
        return Follow(
            id_user = row[id_user].value,
            id_follower = row[id_follower].value
        )
    }

}
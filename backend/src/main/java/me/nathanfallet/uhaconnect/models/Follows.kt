package me.nathanfallet.uhaconnect.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Follows : Table() {
    val user_id = reference("user_id", Users)
    val follower_id = reference("follower_id", Users)

    override val primaryKey = PrimaryKey(user_id, follower_id, name = "PK_follows")

    fun toFollows(row: ResultRow): Follow {
        return Follow(
            user_id = row[user_id].value,
            follower_id = row[follower_id].value
        )
    }

}
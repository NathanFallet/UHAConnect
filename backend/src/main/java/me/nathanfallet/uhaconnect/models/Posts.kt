package me.nathanfallet.uhaconnect.models

import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object Posts : IntIdTable() {
    val user_id = reference("id_user", Users)
    val title = varchar("title", 32)
    val content = text("content")
    val date = long("date")
    val validated = bool("validated").default(false)

    override val primaryKey = PrimaryKey(id)

    fun toPost(row: ResultRow): Post {
        val user = if (row.hasValue(Users.id)) Users.toUser(row)
        else null
        val favorite = if (row.hasValue(Favorites.post_id)) Favorites.toFavorite(row)
        else null
        return Post(
            id = row[id].value,
            user_id = row[user_id].value,
            title = row[title],
            content = row[content],
            date = Instant.fromEpochMilliseconds(row[date]),
            validated = row[validated],
            user = user,
            favorite = favorite
        )
    }
}
package me.nathanfallet.uhaconnect.models

import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object Posts : IntIdTable() {

    val user_id = reference("user_id", Users)
    val title = varchar("title", 255)
    val content = text("content")
    val date = long("date")
    val tag = text("tag")
    val validated = bool("validated").default(false)

    fun toPost(row: ResultRow): Post {
        val user = if (row.hasValue(Users.id)) Users.toUser(row)
        else null
        val favorite =
            if (
                row.hasValue(Favorites.post_id) &&
                row.getOrNull(Favorites.post_id) != null &&
                row.hasValue(Favorites.user_id) &&
                row.getOrNull(Favorites.user_id) != null
            ) Favorites.toFavorite(row)
            else null
        return Post(
            id = row[id].value,
            user_id = row[user_id].value,
            title = row[title],
            content = row[content],
            date = Instant.fromEpochMilliseconds(row[date]),
            tag = row[tag].split(",").map { it.trim() }.filter { it.isNotBlank() },
            validated = row[validated],
            user = user,
            favorite = favorite
        )
    }
}
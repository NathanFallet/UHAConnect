package me.nathanfallet.uhaconnect.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow


object Users : IntIdTable() {

    val username = varchar("username", 64)
    val firstName = varchar("firstName", 64)
    val lastName = varchar("lastName", 64)
    val email = varchar("email", 255)
    val role = varchar("role", 255).default(RoleStatus.STUDENT.toString())
    val password = varchar("password", 255)
    val picture = varchar("picture", 255).nullable()

    fun toUser(row: ResultRow): User {
        val follow =
            if (
                row.hasValue(Follows.user_id) &&
                row.getOrNull(Follows.user_id) != null &&
                row.hasValue(Follows.follower_id) &&
                row.getOrNull(Follows.follower_id) != null
            ) Follows.toFollow(row)
            else null
        return User(
            id = row[id].value,
            username = row[username],
            firstName = row[firstName],
            lastName = row[lastName],
            email = row[email],
            role = RoleStatus.valueOf(row[role]),
            password = row[password],
            picture = row.getOrNull(picture),
            follow = follow
        )
    }

}
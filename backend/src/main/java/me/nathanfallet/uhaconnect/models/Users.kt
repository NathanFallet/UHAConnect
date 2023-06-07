package me.nathanfallet.uhaconnect.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow


object Users : IntIdTable() {

    val username = varchar("username", 64)
    val firstName = varchar("firstName", 64)
    val lastName = varchar("lastName", 64)
    val email = varchar("email", 64)
    val role = varchar("role", 13)
    val password = varchar("password", 128)

    override val primaryKey = PrimaryKey(id)

    fun toUser(row: ResultRow): User {
        return User(
            id = row[id].value,
            username = row[username],
            firstName = row[firstName],
            lastName = row[lastName],
            email = row[email],
            role = RoleStatus.valueOf(row[role]),
            password = row[password]
        )
    }

}
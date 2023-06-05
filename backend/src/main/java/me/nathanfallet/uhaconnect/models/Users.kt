package me.nathanfallet.uhaconnect.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Users : Table() {

    val id = integer("id").autoIncrement()

    override val primaryKey = PrimaryKey(id)

    fun toUser(row: ResultRow): User {
        return User(row[id])
    }

}

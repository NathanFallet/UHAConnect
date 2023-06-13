package me.nathanfallet.uhaconnect.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import kotlin.random.Random

object AuthResetCodes : Table() {

    val userId = reference("user_id", Users.id)
    val code = integer("code")
    val expiration = long("expiration")

    override val primaryKey = PrimaryKey(code)

    fun toCode(row: ResultRow): AuthResetCode {
        return AuthResetCode(
            userId = row[userId].value,
            code = row[code],
            expiration = row[expiration]
        )
    }

    fun generateCode(): Int {
        val candidate = Random.nextInt(100000, 999999)
        return if (select { code eq candidate }.count() > 0) {
            generateCode()
        } else {
            candidate
        }
    }

}
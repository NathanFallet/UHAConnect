package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class UserToken(
    val user: User,
    val token: String
)
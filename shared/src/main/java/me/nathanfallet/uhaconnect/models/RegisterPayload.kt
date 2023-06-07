package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterPayload(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String
)

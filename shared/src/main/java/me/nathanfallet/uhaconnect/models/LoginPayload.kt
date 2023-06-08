package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginPayload(
    val username: String,
    val password: String
)
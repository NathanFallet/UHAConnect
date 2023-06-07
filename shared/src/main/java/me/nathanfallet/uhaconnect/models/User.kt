package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: RoleStatus,
    val password: String,
)

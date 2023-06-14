package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserPayload(
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null,
    val email: String? = null,
    val role: RoleStatus? = null,
    val password: String? = null,
    val picture: String? = null
)

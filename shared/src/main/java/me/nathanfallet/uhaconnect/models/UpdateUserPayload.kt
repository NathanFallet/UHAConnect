package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserPayload(
    var firstName: String?,
    var lastName: String?,
    var username: String?,
    var email: String?,
    var role: RoleStatus?,
    var password: String?
)

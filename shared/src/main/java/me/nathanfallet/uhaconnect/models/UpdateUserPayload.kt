package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserPayload(
    var firstName: String? = null,
    var lastName: String? = null,
    var username: String? = null,
    var email: String? = null,
    var role: RoleStatus? = null,
    var password: String? = null,
    var picture: String? = null
)

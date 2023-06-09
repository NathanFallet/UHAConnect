package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordPayload(
    val email: String? = null,
    val code: Int? = null,
    val password: String? = null
)
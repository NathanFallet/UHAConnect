package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordPayload(
    val email: String?,
    val code: Int?,
    val password: String?
)
package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthResetCode(
    val userId: Int,
    val code: Int,
    val expiration: Long
)

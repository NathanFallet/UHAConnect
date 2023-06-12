package me.nathanfallet.uhaconnect.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class NotificationsToken(
    val token: String,
    val userId: Int?,
    val expiration: Instant,
    val user: User? = null
)

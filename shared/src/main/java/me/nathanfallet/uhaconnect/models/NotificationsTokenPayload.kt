package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class NotificationsTokenPayload(
    val token: String
)
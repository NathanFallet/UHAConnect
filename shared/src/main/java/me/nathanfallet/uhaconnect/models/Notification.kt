package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id_user: Int, // who receives the notification
    val id_post: Int,
    val type: TypeStatus,
    val id_origin: Int, // who creates the notification
)
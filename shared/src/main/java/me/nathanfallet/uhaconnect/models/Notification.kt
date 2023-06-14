package me.nathanfallet.uhaconnect.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: Int,
    val dest_id: Int, // who receives the notification
    val post_id: Int?,
    val type: TypeStatus,
    val origin_id: Int, // who creates the notification
    val date: Instant,
    val user: User? = null
)
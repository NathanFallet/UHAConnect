package me.nathanfallet.uhaconnect.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val post_id: Int,
    val user_id: Int,
    val content: String,
    val date: Instant
)
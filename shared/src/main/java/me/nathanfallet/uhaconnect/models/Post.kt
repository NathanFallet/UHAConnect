package me.nathanfallet.uhaconnect.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: Int,
    val user_id: Int,
    val title: String,
    val content: String,
    val date: Instant,
    val validated: Boolean,
    val user: User? = null,
    val favorite: Favorite? = null
)
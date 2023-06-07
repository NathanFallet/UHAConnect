package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id_post: Int,
    val id_user: Int,
    val content: String
)
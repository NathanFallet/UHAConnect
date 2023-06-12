package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class Favorite(
    val user_id: Int,
    val post_id: Int
)
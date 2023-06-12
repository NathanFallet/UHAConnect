package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class Follow(
    val user_id: Int,
    val follower_id: Int,
)
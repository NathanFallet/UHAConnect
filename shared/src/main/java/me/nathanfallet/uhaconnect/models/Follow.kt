package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class Follow(
    val id_user: Int,
    val id_follower: Int,
)
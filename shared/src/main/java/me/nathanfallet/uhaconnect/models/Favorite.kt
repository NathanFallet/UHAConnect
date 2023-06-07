package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class Favorite(
    val id_user: Int,
    val id_post: Int,
)
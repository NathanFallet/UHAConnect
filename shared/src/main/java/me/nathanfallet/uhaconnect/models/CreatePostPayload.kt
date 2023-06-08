package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class CreatePostPayload(
    val title: String,
    val content: String
)
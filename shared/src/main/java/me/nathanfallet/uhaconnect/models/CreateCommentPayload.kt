package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateCommentPayload(
    val content: String
)
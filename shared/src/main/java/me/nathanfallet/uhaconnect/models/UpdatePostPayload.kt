package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePostPayload(
    val title: String?,
    val content: String?,
    val validated: Boolean?
)
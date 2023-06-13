package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePostPayload(
    val title: String? = null,
    val content: String? = null,
    val validated: Boolean? = null
)
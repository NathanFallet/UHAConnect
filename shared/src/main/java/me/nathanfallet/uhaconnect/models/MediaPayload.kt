package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class MediaPayload(
    val fileName: String
)
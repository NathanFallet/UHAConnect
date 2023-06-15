package me.nathanfallet.uhaconnect.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: Int,
    val user_id: Int,
    val title: String,
    val content: String,
    val date: Instant,
    val tag: List<String>,
    val validated: Boolean,
    val user: User? = null,
    val favorite: Favorite? = null
) {

    companion object {

        fun isTitleValid(title: String): Boolean {
            return title.isNotBlank() && title.length <= 255
        }

    }

}
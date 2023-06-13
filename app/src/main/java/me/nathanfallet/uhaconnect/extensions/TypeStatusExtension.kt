package me.nathanfallet.uhaconnect.extensions

import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.models.TypeStatus

fun TypeStatus.text(): Int {
    return when (this) {
        TypeStatus.LIKE -> R.string.notifications_like
        TypeStatus.COMMENT -> R.string.notifications_comment
        TypeStatus.FOLLOWER -> R.string.notifications_follow
        TypeStatus.NEW_POST -> R.string.notifications_post
    }
}
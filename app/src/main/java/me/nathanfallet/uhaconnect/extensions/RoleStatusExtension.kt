package me.nathanfallet.uhaconnect.extensions

import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.models.RoleStatus

val RoleStatus.text: Int
    get() = when (this) {
        RoleStatus.STUDENT -> R.string.role_student
        RoleStatus.TEACHER -> R.string.role_teacher
        RoleStatus.STAFF -> R.string.role_staff
        RoleStatus.MODERATOR -> R.string.role_moderator
        RoleStatus.ADMINISTRATOR -> R.string.role_administrator
        RoleStatus.BANNED -> R.string.role_banned
    }
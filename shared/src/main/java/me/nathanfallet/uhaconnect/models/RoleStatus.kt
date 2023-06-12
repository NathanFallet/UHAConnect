package me.nathanfallet.uhaconnect.models

enum class RoleStatus {
    ADMINISTRATOR,
    MODERATOR,
    STAFF,
    TEACHER,
    STUDENT;

    fun hasPermission(permission: Permission): Boolean {
        return when (permission) {
            Permission.USER_UPDATE -> listOf(ADMINISTRATOR).contains(this)
            Permission.POST_DELETE -> listOf(ADMINISTRATOR, MODERATOR).contains(this)
            Permission.POST_UPDATE -> listOf(ADMINISTRATOR, MODERATOR).contains(this)
            Permission.COMMENT_DELETE -> listOf(ADMINISTRATOR, MODERATOR).contains(this)
        }
    }

}
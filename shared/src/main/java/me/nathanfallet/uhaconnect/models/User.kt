package me.nathanfallet.uhaconnect.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: RoleStatus,
    val password: String,
) {

    companion object {

        fun isEmailValid(email: String): Boolean {
            return Regex("[a-zA-Z0-9]+\\.[a-zA-Z0-9]+@uha\\.fr").matches(email)
        }

    }

}

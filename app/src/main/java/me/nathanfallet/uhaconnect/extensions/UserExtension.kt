package me.nathanfallet.uhaconnect.extensions

import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.services.APIService

val User.pictureUrl: String
    get() = "${APIService.baseUrl}/media/${picture}"
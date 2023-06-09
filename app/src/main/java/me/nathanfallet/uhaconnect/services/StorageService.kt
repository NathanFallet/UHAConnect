package me.nathanfallet.uhaconnect.services

import android.content.Context
import me.nathanfallet.uhaconnect.utils.SingletonHolder

class StorageService private constructor(val context: Context) {

    // Shared instance

    companion object : SingletonHolder<StorageService, Context>(::StorageService)

    // Storage

    val sharedPreferences = context.getSharedPreferences("uhaconnect", Context.MODE_PRIVATE)

}
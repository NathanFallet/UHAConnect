package me.nathanfallet.uhaconnect.features.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.Notification
import me.nathanfallet.uhaconnect.services.APIService


class NotificationViewModel() : ViewModel() {

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>>
        get() = _notifications

    private val _hasMore = MutableLiveData(true)
    val hasMore: LiveData<Boolean>
        get() = _hasMore

    fun loadData(token: String?, reset: Boolean) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val offset = (if (reset) 0 else _notifications.value?.size ?: 0).toLong()
                APIService.getInstance(Unit).getNotifications(token, offset).let {
                    _notifications.value =
                        if (reset) it
                        else (_notifications.value ?: listOf()) + it
                    _hasMore.value = it.isNotEmpty()
                }
            } catch (e: Exception) {

            }
        }
    }
}






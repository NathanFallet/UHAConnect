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
    val notifications: LiveData<List<Notification>> get() = _notifications

    fun loadData(token: String?) {
        if (token == null) return
        viewModelScope.launch {
            _notifications.value = APIService.getInstance(Unit).getNotification(token)
        }
    }
}






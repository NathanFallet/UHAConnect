package me.nathanfallet.uhaconnect.features

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.models.UserToken
import me.nathanfallet.uhaconnect.services.APIService
import me.nathanfallet.uhaconnect.services.StorageService

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _token = MutableLiveData<String>()
    val token: LiveData<String>
        get() = _token


    init {
        // Load user and token, if connected
        val prefs = StorageService.getInstance(getApplication()).sharedPreferences
        try {
            prefs.getString("user", null)?.let {
                _user.value = Json.decodeFromString(it)
            }
            prefs.getString("token", null)?.let {
                _token.value = it
            }
        } catch (e: Exception) {
            login(null)
        }

        // Setup firebase messaging
        setupFirebaseMessaging()
    }

    fun setupFirebaseMessaging() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                token.value?.let { token ->
                    it.result?.let { fcmToken ->
                        viewModelScope.launch {
                            try {
                                APIService.getInstance(Unit).sendNotificationToken(
                                    token, fcmToken
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
        FirebaseMessaging.getInstance().subscribeToTopic("broadcast")
    }

    fun login(userToken: UserToken?) {
        _user.value = userToken?.user
        _token.value = userToken?.token

        // Token is invalid, remove it
        if (userToken == null) {
            StorageService.getInstance(getApplication()).sharedPreferences
                .edit()
                .remove("user")
                .remove("token")
                .apply()
            return
        }

        // Else, save new values
        StorageService.getInstance(getApplication()).sharedPreferences
            .edit()
            .putString("user", Json.encodeToString(userToken.user))
            .putString("token", userToken.token)
            .apply()
    }

}
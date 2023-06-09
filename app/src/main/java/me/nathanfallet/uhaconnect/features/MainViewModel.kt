package me.nathanfallet.uhaconnect.features

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.models.UserToken
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
        prefs.getString("user", null)?.let {
            _user.value = Json.decodeFromString(it)
        }
        prefs.getString("token", null)?.let {
            _token.value = it
        }
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

    /*fun resetPassword(email: String) {
        viewModelScope.launch {
            val userToken = APIService.getInstance().ResetPassword(email)
            userToken?.let {
                _user.value = it.user
                _token.value = it.token
            }
        }
    }*/

}
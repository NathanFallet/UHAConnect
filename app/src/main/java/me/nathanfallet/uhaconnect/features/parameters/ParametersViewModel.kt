package me.nathanfallet.uhaconnect.features.parameters

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.models.UpdateUserPayload
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.services.APIService

class ParametersViewModel: ViewModel() {

    val error = MutableLiveData<Int>()
    val newUsername = MutableLiveData("")
    val newPassword = MutableLiveData("")
    val newPassword2 = MutableLiveData("")

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun changeUsername(token: String?, id: Int?){
        if (token == null || id == null) return
        if (isUsernameValid()) {
            viewModelScope.launch {
                try {
                    _user.value = APIService.getInstance(Unit).updateUser(
                        token,
                        id,
                        UpdateUserPayload(
                            username = newUsername.value ?: "",
                        )
                    )
                } catch (e: Exception) {
                    Log.d("ParametersViewModel", e.toString())
                    error.value = R.string.parameters_invalid_un_credentials
                }
            }
        } else null ?: run {
            Log.d("ParametersViewModel", "UsernameNotValid")
            error.value = R.string.parameters_invalid_un_credentials
        }
    }

    fun changePassword(token: String?, id: Int?){
        if (token == null || id == null) return
        if (isPasswordValid()) {
            viewModelScope.launch {
                try {
                    _user.value = APIService.getInstance(Unit).updateUser(
                        token,
                        id,
                        UpdateUserPayload(
                            newPassword.value ?: ""
                        )
                    )
                } catch (e: Exception) {
                    Log.d("ParametersViewModel", e.toString())
                    error.value = R.string.parameters_invalid_pw_credentials
                }
            }
        } else null ?: run {
            Log.d("ParametersViewModel", "UsernameNotValid")
            error.value = R.string.parameters_invalid_pw_credentials
        }
    }

    fun selectMedia(token: String?, id: Int?, uri: Uri?, context: Context) {
        if (token == null || id == null || uri == null) return
        viewModelScope.launch {
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use {
                    it.readBytes()
                } ?: ByteArray(0)
                val payload = APIService.getInstance(Unit).uploadMedia(token, bytes, false)
                _user.value = APIService.getInstance(Unit).updateUser(
                    token, id, UpdateUserPayload(picture = payload.fileName)
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception, e.g., show an error message or retry
            }
        }
    }

    private fun isUsernameValid(): Boolean {
        return User.isUsernameValid(newUsername.value ?: "")
    }

    private fun isPasswordValid(): Boolean {
        return ((newPassword.value?.length ?: 0) >= 6) && (newPassword.value == newPassword2.value)
    }

}
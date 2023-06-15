package me.nathanfallet.uhaconnect.features.parameters

import android.content.Context
import android.net.Uri
import android.util.Log
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
    val success = MutableLiveData<Int>()
    val newUsername = MutableLiveData("")
    val newPassword = MutableLiveData("")
    val newPassword2 = MutableLiveData("")

    fun changeUsername(token: String?, id: Int?, onUpdateUser: (User) -> Unit) {
        if (token == null || id == null) return
        if (!User.isUsernameValid(newUsername.value ?: "")) {
            error.value = R.string.parameters_invalid_un_credentials
            return
        }
        viewModelScope.launch {
            try {
                onUpdateUser(
                    APIService.getInstance(Unit).updateUser(
                        token,
                        id,
                        UpdateUserPayload(username = newUsername.value ?: "")
                    )
                )
                success.value = R.string.parameters_username_updated
            } catch (e: Exception) {
                error.value = R.string.parameters_invalid_un_credentials
            }
        }
    }

    fun changePassword(token: String?, id: Int?, onUpdateUser: (User) -> Unit) {
        if (token == null || id == null) return
        if (!isPasswordValid()) {
            error.value = R.string.parameters_invalid_pw_credentials
            return
        }
        viewModelScope.launch {
            try {
                onUpdateUser(
                    APIService.getInstance(Unit).updateUser(
                        token,
                        id,
                        UpdateUserPayload(password = newPassword.value ?: "")
                    )
                )
                success.value = R.string.parameters_password_updated
            } catch (e: Exception) {
                Log.d("ParametersViewModel", e.toString())
                error.value = R.string.parameters_invalid_pw_credentials
            }
        }
    }

    fun selectMedia(
        token: String?,
        id: Int?,
        uri: Uri?,
        context: Context,
        onUpdateUser: (User) -> Unit
    ) {
        if (token == null || id == null || uri == null) return
        viewModelScope.launch {
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use {
                    it.readBytes()
                } ?: ByteArray(0)
                val payload = APIService.getInstance(Unit).uploadMedia(token, bytes, false)
                onUpdateUser(
                    APIService.getInstance(Unit).updateUser(
                        token, id, UpdateUserPayload(picture = payload.fileName)
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception, e.g., show an error message or retry
            }
        }
    }

    private fun isPasswordValid(): Boolean {
        return ((newPassword.value?.length ?: 0) >= 6) && (newPassword.value == newPassword2.value)
    }

}
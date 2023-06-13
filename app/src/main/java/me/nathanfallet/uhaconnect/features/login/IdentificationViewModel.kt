package me.nathanfallet.uhaconnect.features.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.models.LoginPayload
import me.nathanfallet.uhaconnect.models.RegisterPayload
import me.nathanfallet.uhaconnect.models.ResetPasswordPayload
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.models.UserToken
import me.nathanfallet.uhaconnect.services.APIService

class IdentificationViewModel : ViewModel() {

    val error = MutableLiveData<Int>()
    val username = MutableLiveData("")
    val password = MutableLiveData("")
    val password2 = MutableLiveData("")
    val firstname = MutableLiveData("")
    val lastname = MutableLiveData("")
    val mail = MutableLiveData("")
    val code = MutableLiveData("")

    fun login(loginUser: (UserToken) -> Unit) {
        if (!User.isUsernameValid(username.value ?: "")) {
            error.value = R.string.login_invalid_username
            return
        }
        if (!isPasswordValid()) {
            error.value = R.string.login_invalid_password
            return
        }
        viewModelScope.launch {
            try {
                APIService.getInstance(Unit).login(
                    LoginPayload(
                        username.value ?: "",
                        password.value ?: ""
                    )
                ).let {
                    loginUser(it)
                }
            } catch (e: Exception) {
                error.value = R.string.login_invalid_credentials
            }
        }
    }

    fun createAccount(loginUser: (UserToken) -> Unit) {
        if (firstname.value?.isNotBlank() == true) {
            error.value = R.string.login_missing_fields
            return
        }
        if (lastname.value?.isNotBlank() == true) {
            error.value = R.string.login_missing_fields
            return
        }
        if (!User.isUsernameValid(username.value ?: "")) {
            error.value = R.string.login_invalid_username
            return
        }
        if (!isPasswordValid()) {
            error.value = R.string.login_invalid_password
            return
        }
        if (password.value != password2.value) {
            error.value = R.string.login_passwords_not_match
            return
        }
        if (!User.isEmailValid(mail.value ?: "")) {
            error.value = R.string.login_invalid_mail
            return
        }
        viewModelScope.launch {
            try {
                APIService.getInstance(Unit).createAccount(
                    RegisterPayload(
                        firstname.value ?: "",
                        lastname.value ?: "",
                        username.value ?: "",
                        mail.value ?: "",
                        password.value ?: ""
                    )
                ).let {
                    loginUser(it)
                }
            } catch (e: Exception) {
                error.value = R.string.login_invalid_credentials
            }
        }
    }

    fun sendMail(navigate: (String) -> Unit) {
        if (!User.isEmailValid(mail.value ?: "")) {
            error.value = R.string.login_invalid_mail
            return
        }
        viewModelScope.launch {
            try {
                APIService.getInstance(Unit)
                    .resetPassword(ResetPasswordPayload(email = mail.value))
                navigate("resetPassword")
            } catch (e: java.lang.Exception) {
                error.value = R.string.login_email_wrong
            }
        }
    }

    fun resetPassword(navigate: (String) -> Unit){
        viewModelScope.launch {
            try {
                APIService.getInstance(Unit)
                    .resetPassword(
                        ResetPasswordPayload(
                            code = code.value?.toInt(),
                            password = password.value
                        )
                    )
                navigate("login")
            } catch (e: Exception) {
                error.value = R.string.login_code_pw_wrong
            }
        }
    }

    private fun isPasswordValid(): Boolean {
        // TODO: Move check to shared, like username and mail
        return (password.value?.length ?: 0) >= 6
    }

}
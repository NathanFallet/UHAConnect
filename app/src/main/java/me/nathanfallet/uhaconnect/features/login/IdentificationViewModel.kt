package me.nathanfallet.uhaconnect.features.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.R.string.login_invalid_credentials
import me.nathanfallet.uhaconnect.models.*
import me.nathanfallet.uhaconnect.services.APIService

class LoginViewModel : ViewModel() {

    val error = MutableLiveData<Int>()
    val username = MutableLiveData("")
    val password = MutableLiveData("")
    val password2 = MutableLiveData("")
    val firstname = MutableLiveData("")
    val lastname = MutableLiveData("")
    val mail = MutableLiveData("")

    fun login(loginUser: (UserToken) -> Unit) {
        if (validateLoginForm()) {
            viewModelScope.launch {
                try {
                    APIService.getInstance(Unit).login(
                        LoginPayload(
                            username.value ?: "", password.value ?: ""
                        )
                    ).let {
                        loginUser(it)
                    }
                } catch (e: Exception) {
                    Log.d("LoginViewModel", e.toString())
                    error.value = login_invalid_credentials
                }
            }
        } else {
            if (!isUsernameValid()) {
                error.value = R.string.login_invalid_username
            } else if (!isPasswordValid()) {
                error.value = R.string.login_invalid_password
            }
        }
    }

    fun createAccount(loginUser: (UserToken) -> Unit) {
        if (validateCreateAccountForm()) {
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
        } else {
            if (password.value != password2.value) {
                error.value = R.string.login_passwords_not_match
            } else if (isMailValid()) {
                error.value = R.string.login_invalid_mail
            }
        }
    }

    fun resetPassword() {
        if (validateResetPasswordForm()) {
            println("Demande de réinitialisation du mot de passe envoyée à l'adresse : $mail")
        } else {
            println("Impossible de réinitialiser le mot de passe : veuillez vérifier les informations fournies")
        }
    }

    private fun validateLoginForm(): Boolean {
        return isUsernameValid() && isPasswordValid()
    }

    private fun isUsernameValid(): Boolean {
        return User.isUsernameValid(username.value ?: "")
    }

    private fun isPasswordValid(): Boolean {
        return (password.value?.length ?: 0) >= 6
    }

    private fun isMailValid(): Boolean {
        return User.isEmailValid(mail.value ?: "")
    }

    private fun validateCreateAccountForm(): Boolean {
        return firstname.value?.isNotBlank() == true && lastname.value?.isNotBlank() == true && isUsernameValid() && isPasswordValid() && isMailValid()
    }

    private fun validateResetPasswordForm(): Boolean {
        return mail.value?.isNotBlank() ?: false
    }
}
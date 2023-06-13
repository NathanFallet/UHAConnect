package me.nathanfallet.uhaconnect.features.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.common.api.Api
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.models.*
import me.nathanfallet.uhaconnect.models.User.Companion.isUsernameValid
import me.nathanfallet.uhaconnect.services.APIService

class LoginViewModel : ViewModel() {

    val error = MutableLiveData<Int>()
    val username = MutableLiveData("")
    val password = MutableLiveData("")
    val password2 = MutableLiveData("")
    val firstname = MutableLiveData("")
    val lastname = MutableLiveData("")
    val mail = MutableLiveData("")
    val code = MutableLiveData("")

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
                    error.value = R.string.login_invalid_credentials
                }
            }
        } else null ?: run {
            Log.d("LoginViewModel", "validateLoginForm")
            error.value = R.string.login_invalid_credentials
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
        } else null ?: run {
            error.value = R.string.login_invalid_credentials
        }
    }


    fun sendMail(navigate: (String) -> Unit) {
        viewModelScope.launch {
            try {
                APIService.getInstance(Unit)
                    .resetPassword(ResetPasswordPayload(email = mail.value)
                    ).let {
                        navigate("resetPassword")
                    }
            }
            catch (e: java.lang.Exception) {
                error.value = R.string.login_email_wrong
            }
        }
    }

    fun resetPassword(navigate: (String) -> Unit){
        viewModelScope.launch {
            try{
                APIService.getInstance(Unit)
                    .resetPassword(ResetPasswordPayload(
                        code = code.value?.toInt(),
                        password = password.value)
                    ).let {
                        navigate("login")
                    }
            }
            catch (e: Exception){
                error.value = R.string.login_code_pw_wrong
            }
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
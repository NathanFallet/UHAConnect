package me.nathanfallet.uhaconnect.features.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.LoginPayload
import me.nathanfallet.uhaconnect.models.RegisterPayload
import me.nathanfallet.uhaconnect.models.UserToken
import me.nathanfallet.uhaconnect.services.APIService

class LoginViewModel(
    val loginUser: (UserToken) -> Unit
) : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var firstname by mutableStateOf("")
    var lastname by mutableStateOf("")
    var mail by mutableStateOf("")

    fun login() {
        if (validateLoginForm()) {
            viewModelScope.launch {
                APIService.getInstance(Unit).login(
                    LoginPayload(
                        username, password
                    )
                )?.let {
                    loginUser(it)
                    println("Connexion réussie pour l'utilisateur : $username")
                } ?: run {

                    println("Échec de la connexion ")
                }
            }
        } else {
            println("Veuillez saisir un nom d'utilisateur et un mot de passe valides")
        }
    }

    fun createAccount() {
        if (validateCreateAccountForm()) {
            viewModelScope.launch {
                val userToken =
                    APIService.getInstance(Unit).createAccount(
                        RegisterPayload(
                            firstname, lastname, username, mail, password
                        )
                    )
                userToken?.let {
                    loginUser(userToken)
                } ?: run {

                }
                println("Création de compte réussie : $firstname $lastname")
            }
        } else {
            println("Impossible de créer le compte : veuillez vérifier les informations fournies")
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
        val usernameRegex = Regex("[a-zA-Z0-9]{4,}")
        return username.matches(usernameRegex)
    }

    private fun isPasswordValid(): Boolean {
        return password.length >= 6
    }

    private fun validateCreateAccountForm(): Boolean {
        return firstname.isNotBlank() && lastname.isNotBlank() && username.isNotBlank() && password.isNotBlank() && mail.isNotBlank()
    }

    private fun validateResetPasswordForm(): Boolean {
        return mail.isNotBlank()
    }
}
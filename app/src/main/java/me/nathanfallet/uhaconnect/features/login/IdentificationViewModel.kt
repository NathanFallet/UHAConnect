package me.nathanfallet.uhaconnect.features.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.services.APIService
import me.nathanfallet.uhaconnect.features.login.ui.theme.UHAConnectTheme

class LoginViewModel : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var firstname by mutableStateOf("")
    var lastname by mutableStateOf("")
    var mail by mutableStateOf("")

    fun login() {
        if (validateLoginForm()) {
            viewModelScope.launch {
                // Effectuer l'appel à l'API pour la connexion
                val result = APIService.getInstance().login(username, password)
                if (result.success) {
                    println("Connexion réussie pour l'utilisateur : $username")
                } else {
                    println("Échec de la connexion : ${result.errorMessage}")
                }
            }
        } else {
            println("Veuillez saisir un nom d'utilisateur et un mot de passe valides")
        }
    }

    fun createAccount() {
        if (validateCreateAccountForm()) {
            println("Création de compte réussie : $firstname $lastname")
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

class LoginActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UHAConnectTheme {
                Surface {
                    LoginScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var mail by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Login Form")
        // Composants pour la saisie des informations de connexion (username, password)
        // Utilisez la variable locale "username" et "password" pour la saisie des valeurs

        Button(onClick = {
            viewModel.username = username
            viewModel.password = password
            viewModel.login()
        }) {
            Text(text = "Login")
        }

        Text(text = "Create Account Form")
        // Composants pour la saisie des informations de création de compte (firstname, lastname, username, password, mail)
        // Utilisez les variables locales "firstname", "lastname", "username", "password" et "mail" pour la saisie des valeurs

        Button(onClick = {
            viewModel.firstname = firstname
            viewModel.lastname = lastname
            viewModel.username = username
            viewModel.password = password
            viewModel.mail = mail
            viewModel.createAccount()
        }) {
            Text(text = "Create Account")
        }

        Text(text = "Reset Password Form")
        // Composants pour la saisie des informations de réinitialisation du mot de passe (mail)
        // Utilisez la variable locale "mail" pour la saisie de la valeur

        Button(onClick = {
            viewModel.mail = mail
            viewModel.resetPassword()
        }) {
            Text(text = "Reset Password")
        }
    }
}

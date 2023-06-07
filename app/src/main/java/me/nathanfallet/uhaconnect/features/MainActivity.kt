package me.nathanfallet.uhaconnect.features


import CreateAccountPage
import LoginPage
import ResetPasswordPage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.nathanfallet.uhaconnect.features.Favs.FavsView
import me.nathanfallet.uhaconnect.features.home.HomeView
import me.nathanfallet.uhaconnect.features.notifications.NotificationView
import me.nathanfallet.uhaconnect.features.post.PostView
import me.nathanfallet.uhaconnect.ui.theme.UHAConnectTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UHAConnectApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun UHAConnectApp() {
    UHAConnectTheme {

        val viewModel: MainViewModel = viewModel()

        val navController = rememberNavController()
        val user by viewModel.user.observeAsState()

        Scaffold { padding ->
            NavHost(
                navController = navController,
                // TODO: Change startDestination to "login" when login is implemented
                startDestination = if (user != null) "home" else "home"
            ) {
                composable("home") {
                    HomeView(
                        modifier = Modifier.padding(padding)
                    )
                }

                composable("login") {
                    LoginPage(
                        navigate = navController::navigate,
                        onCreateAccountClick = { navController.navigate("createAccount") },
                        onResetPasswordClick = { navController.navigate("resetPassword") }
                    )
                }
                composable("createAccount") {
                    CreateAccountPage(navigate = { destination: String -> navController.navigate(destination) })
                }
                composable("resetPassword") {
                    ResetPasswordPage(navigate = { destination: String -> navController.navigate(destination) })

                }


                composable("notifications") {
                    NotificationView()
                }
                composable("post") {
                    PostView(
                        modifier = Modifier.padding(padding)
                    )
                }
                composable("favs") {
                    FavsView(
                        modifier = Modifier.padding(padding)
                    )
                }

            }
        }
    }
}

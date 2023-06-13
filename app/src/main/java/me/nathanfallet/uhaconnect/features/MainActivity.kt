package me.nathanfallet.uhaconnect.features

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.features.compose.ComposeView
import me.nathanfallet.uhaconnect.features.feed.FeedView
import me.nathanfallet.uhaconnect.features.login.CreateAccountPage
import me.nathanfallet.uhaconnect.features.login.LoginPage
import me.nathanfallet.uhaconnect.features.login.ResetEmailPage
import me.nathanfallet.uhaconnect.features.login.ResetPasswordPage
import me.nathanfallet.uhaconnect.features.notifications.NotificationView
import me.nathanfallet.uhaconnect.features.parameters.ParametersView
import me.nathanfallet.uhaconnect.features.post.PostView
import me.nathanfallet.uhaconnect.features.profile.ProfileView
import me.nathanfallet.uhaconnect.ui.theme.UHAConnectTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()

        setContent {
            UHAConnectApp()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

}

enum class NavigationItem(
    val route: String,
    val icon: Int,
    val title: Int
) {

    FEED(
        "feed",
        R.drawable.home,
        R.string.title_activity_main
    ),
    NOTIFICATIONS(
        "notifications",
        R.drawable.notification,
        R.string.title_activity_notification
    ),
    PROFILE(
        "self_profile",
        R.drawable.profile,
        R.string.title_activity_profile_view
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun UHAConnectApp() {
    UHAConnectTheme{

        val viewModel: MainViewModel = viewModel()

        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        val user by viewModel.user.observeAsState()
        val token by viewModel.token.observeAsState()

        Scaffold(
            bottomBar = {
                if (token == null) return@Scaffold
                NavigationBar {
                    val currentRoute = navBackStackEntry?.destination?.route
                    NavigationItem
                        .values()
                        .forEach { item ->
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        painterResource(item.icon),
                                        contentDescription = stringResource(item.title)
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(item.title),
                                        fontSize = 9.sp
                                    )
                                },
                                alwaysShowLabel = true,
                                selected = currentRoute?.startsWith(item.route) ?: false,
                                onClick = {
                                    navController.navigate(item.route) {
                                        navController.graph.startDestinationRoute?.let { route ->
                                            popUpTo(route) {
                                                saveState = true
                                            }
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = if (token != null) "feed" else "login"
            ) {
                composable("login") {
                    LoginPage(
                        modifier = Modifier.padding(padding),
                        navigate = navController::navigate
                    ) { token ->
                        viewModel.login(token)
                        navController.navigate("home")
                    }
                }
                composable("createAccount") {
                    CreateAccountPage(navigate = navController::navigate) { token ->
                        viewModel.login(token)
                        navController.navigate("home")
                    }
                }
                composable("resetPasswordMail") {
                    ResetEmailPage(navigate = navController::navigate)
                }
                composable("resetPassword") {
                    ResetPasswordPage(navigate = navController::navigate)
                }
                composable("notifications") {
                    NotificationView(
                        modifier = Modifier.padding(padding),
                        token = token
                    )
                }
                composable(
                    "post/{postId}",
                    arguments = listOf(navArgument("postId") { type = NavType.IntType })
                ) {
                    PostView(
                        modifier = Modifier.padding(padding),
                        navigate = navController::navigate,
                        token = token,
                        viewedBy = user
                    )
                }
                composable("feed/compose") {
                    ComposeView(
                        modifier = Modifier.padding(padding),
                        token = token,
                        navigate = navController::navigate
                    )
                }
                composable(
                    "feed/{loader}",
                    arguments = listOf(navArgument("loader") { type = NavType.StringType })
                )
                {
                    FeedView(
                        modifier = Modifier.padding(padding),
                        navigate = navController::navigate,
                        token = token,
                        user = user
                    )
                }
                dialog("feed") {
                    navController.navigate("feed/posts")
                }
                dialog("self_profile")
                {
                    val userId = user?.id ?: ""
                    navController.navigate("profile/$userId")
                }
                composable("profile/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.IntType })
                ) {
                    ProfileView(
                        modifier = Modifier.padding(padding),
                        navigate = navController::navigate,
                        token = token,
                        disconnect = {
                            viewModel.login(null)
                            navController.navigate("login")
                        },
                        viewedBy = user
                    )
                }
                composable("settings"){
                    ParametersView(
                        modifier = Modifier.padding(padding),
                        token,
                        user,
                        navController::navigate
                    )
                }
            }
        }
    }
}
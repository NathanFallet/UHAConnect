package me.nathanfallet.uhaconnect.features

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.features.compose.ComposeView
import me.nathanfallet.uhaconnect.features.feed.FeedView
import me.nathanfallet.uhaconnect.features.home.HomeView
import me.nathanfallet.uhaconnect.features.login.CreateAccountPage
import me.nathanfallet.uhaconnect.features.login.LoginPage
import me.nathanfallet.uhaconnect.features.login.ResetPasswordPage
import me.nathanfallet.uhaconnect.features.notifications.NotificationView
import me.nathanfallet.uhaconnect.features.post.PostView
import me.nathanfallet.uhaconnect.features.post.PostViewModel
import me.nathanfallet.uhaconnect.features.profile.ProfileView
import me.nathanfallet.uhaconnect.ui.theme.UHAConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UHAConnectApp()
        }
    }
}

enum class NavigationItem(
    val route: String,
    val icon: ImageVector,
    val title: Int
) {

    HOME(
        "home",
        Icons.Filled.Home,
        R.string.title_activity_main
    ),
    FAVS(
        "feed",
        Icons.Filled.Home,
        R.string.title_activity_favs_view
    ),
    COMPOSE(
        "compose",
        Icons.Filled.Home,
        R.string.title_activity_favs_view
    ),
    POSTS(
        "post",
        Icons.Filled.Home,
        R.string.title_activity_post_view
    ),
    PROFILE(
        "home",
        Icons.Filled.Home,
        R.string.title_activity_post_view
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun UHAConnectApp() {
    UHAConnectTheme {

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
                                        item.icon,
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
                startDestination = if (token != null) "home" else "login"
            ) {
                composable("home") {
                    HomeView(
                        modifier = Modifier.padding(padding)
                    )
                }
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
                composable("resetPassword") {
                    ResetPasswordPage(navigate = navController::navigate)

                }
                composable("notifications") {
                    NotificationView(
                        ""
                    )
                }
                composable("post") {
                    val viewModel = PostViewModel(token!!)
                    PostView(
                        modifier = Modifier.padding(padding),
                        viewModel = viewModel
                    )
                }
                composable("feed") {
                    FeedView(
                        modifier = Modifier.padding(padding),
                        navigate = navController::navigate,
                        token = token
                    )
                }
                composable("compose") {
                    ComposeView(
                        modifier = Modifier.padding(padding),
                        token = token,
                        navigate = navController::navigate
                    )
                }
                composable("profile/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.IntType })){
                    ProfileView(
                        modifier = Modifier.padding(padding),
                        navigate = navController::navigate,
                        token = token)
                }
            }
        }
    }
}
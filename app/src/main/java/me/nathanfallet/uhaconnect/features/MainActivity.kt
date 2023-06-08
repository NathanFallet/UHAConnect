package me.nathanfallet.uhaconnect.features

import me.nathanfallet.uhaconnect.features.post.PostViewModel
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.features.Favs.FavsView
import me.nathanfallet.uhaconnect.features.Favs.FavsViewModel
import me.nathanfallet.uhaconnect.features.home.HomeView
import me.nathanfallet.uhaconnect.features.login.CreateAccountPage
import me.nathanfallet.uhaconnect.features.login.LoginPage
import me.nathanfallet.uhaconnect.features.login.ResetPasswordPage
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
        "favs",
        Icons.Filled.Home,
        R.string.title_activity_favs_view
    ),
    POST(
        "post",
        Icons.Filled.Home,
        R.string.title_activity_post_view
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
                // /TODO: Change startDestination to "login" when login is implemented
                startDestination = if (user != null) "home" else "home"
            ) {
                composable("home") {
                    HomeView(
                        modifier = Modifier.padding(padding)
                    )
                }
                composable("notifications") {
                    NotificationView()
                }
                composable("post") {
                    val viewModel = PostViewModel(token!!)
                    PostView(
                        modifier = Modifier.padding(padding),
                        viewModel = viewModel
                    )
                }
                composable("favs") {
                    val viewModel = FavsViewModel()
                    FavsView(
                        modifier = Modifier.padding(padding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
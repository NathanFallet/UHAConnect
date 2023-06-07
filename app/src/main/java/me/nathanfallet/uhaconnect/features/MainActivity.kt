package me.nathanfallet.uhaconnect.features

import PostView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.nathanfallet.uhaconnect.features.Favs.FavsView
import me.nathanfallet.uhaconnect.features.home.HomeView
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

        val navController = rememberNavController()

        Scaffold { padding ->
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeView(
                        modifier = Modifier.padding(padding)
                    )
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
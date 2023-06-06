import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TopBar(
    title: String,
    onBackClicked: () -> Unit
) {
    TopAppBar(
        title = { Text(text = title, color = Color.White) },
        navigationIcon = {
            IconButton(onClick = { onBackClicked() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Action du profil */ }) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Profile",
                    tint = Color.White
                )
            }
        },
        backgroundColor = Color.Blue
    )
}

@Composable
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(title = "UHAConnect", onBackClicked = { /* Action de retour */ })

            Text(
                text = "Exemple Titre",
                style = MaterialTheme.typography.h6,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Reste du contenu de votre application
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    App()
}

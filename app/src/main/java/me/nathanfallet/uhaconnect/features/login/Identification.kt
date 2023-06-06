import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.nathanfallet.uhaconnect.ui.theme.UHAConnectTheme

class Identification : ComponentActivity() {
    private var showCreateAccountPage by mutableStateOf(false)
    private var showResetPasswordPage by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UHAConnectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showCreateAccountPage) {
                        CreateAccountPage()
                    } else if (showResetPasswordPage) {
                        ResetPasswordPage()
                    } else {
                        LoginPage(
                            onCreateAccountClick = { showCreateAccountPage = true },
                            onResetPasswordClick = { showResetPasswordPage = true }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(
    onCreateAccountClick: () -> Unit,
    onResetPasswordClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val customFontFamily = null

        Text(
            text = "UHA Connect",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            modifier = Modifier.padding(vertical = 120.dp)
        )

        var username by remember { mutableStateOf("") }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nom d'utilisateur") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(
                    color = Color.Gray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                )
        )

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Mot de passe") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
                .background(
                    color = Color.Gray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(top = 0.dp, bottom = 0.dp)
        )

        ClickableText(
            text = AnnotatedString("Mot de passe oublié?"),
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline),
            modifier = Modifier.padding(top = 8.dp),
            onClick = { onResetPasswordClick() }
        )

        ClickableText(
            text = AnnotatedString("Créer un compte"),
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline),
            modifier = Modifier.padding(top = 20.dp),
            onClick = { offset ->
                if (offset != null) {
                    onCreateAccountClick()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Créer un compte",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            modifier = Modifier.padding(vertical = 50.dp)
        )
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var mail by remember { mutableStateOf("") }
        var confirmMail by remember { mutableStateOf("") }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nom d'utilisateur") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(
                    color = Color.Gray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                )

        )

        OutlinedTextField(
            value = mail,
            onValueChange = { mail = it },
            label = { Text("Mail") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(
                    color = Color.Gray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                )
        )

        OutlinedTextField(
            value = confirmMail,
            onValueChange = { confirmMail = it },
            label = { Text("Confirmez le mail") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(
                    color = Color.Gray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(
                    color = Color.Gray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                )
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmez le mot de passe") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(
                    color = Color.Gray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                )
        )

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Blue.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 0.dp, horizontal = 0.dp)
        ) {
            Text(
                text = "Créer votre compte",
                color = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(17.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Réinitialisation du mot de passe",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            ),
            modifier = Modifier.padding(vertical = 130.dp)
        )
        var email by remember { mutableStateOf("") }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Adresse e-mail") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(
                    color = Color.Gray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                )
        )

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Blue.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 1.dp, horizontal = 0.dp)
        ) {
            Text(
                text = "Réinitialiser le mot de passe",
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    UHAConnectTheme {
        LoginPage({}, {})
    }
}

@Preview(showBackground = true)
@Composable
fun CreateAccountPagePreview() {
    UHAConnectTheme {
        CreateAccountPage()
    }
}

@Preview(showBackground = true)
@Composable
fun ResetPasswordPagePreview() {
    UHAConnectTheme {
        ResetPasswordPage()
    }
}
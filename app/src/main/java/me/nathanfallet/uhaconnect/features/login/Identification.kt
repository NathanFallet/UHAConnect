package me.nathanfallet.uhaconnect.features.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.nathanfallet.uhaconnect.models.UserToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
    login: (UserToken) -> Unit
) {

    val viewModel = viewModel<LoginViewModel>()

    val error by viewModel.error.observeAsState()
    val username by viewModel.username.observeAsState("")
    val password by viewModel.password.observeAsState("")

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "UHA Connect",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            modifier = Modifier.padding(bottom = 120.dp)
        )
        error?.let {
            Text(
                text = stringResource(id = it),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Red
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        OutlinedTextField(
            value = username,
            onValueChange = { viewModel.username.value = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            keyboardActions = KeyboardActions(),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = FontFamily.Default,
                fontSize = 14.sp
            ),
            singleLine = true,
            maxLines = 1
        )
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.password.value = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
                .padding(top = 0.dp, bottom = 0.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            ClickableText(
                text = AnnotatedString("Forgotten your password?"),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center
                ),
                onClick = { navigate("resetPassword") }
            )
        }

        Button(
            onClick = {
                viewModel.login(login)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 33.dp, horizontal = 2.dp)
        ) {
            Text(
                text = "Log in",
                color = Color.Black
            )
        }

        ClickableText(
            text = AnnotatedString.Builder().apply {
                pushStyle(style = SpanStyle(color = Color.Black))
                append("You don't have an account? ")
                pushStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline))
                append("Join us")
                pop()
            }.toAnnotatedString(),
            modifier = Modifier.padding(top = 20.dp,bottom = 40.dp),
            onClick = { offset ->
                if (offset >= 27) {
                    navigate("createAccount")
                }
            }
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountPage(
    navigate: (String) -> Unit,
    login: (UserToken) -> Unit
) {

    val viewModel = viewModel<LoginViewModel>()

    val error by viewModel.error.observeAsState()
    val firstname by viewModel.firstname.observeAsState("")
    val lastname by viewModel.lastname.observeAsState("")
    val mail by viewModel.mail.observeAsState("")
    val username by viewModel.username.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val password2 by viewModel.password2.observeAsState("")

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sign up",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            modifier = Modifier.padding(vertical = 50.dp)
        )

        error?.let {
            Text(
                text = stringResource(id = it),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Red
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = firstname,
                onValueChange = { viewModel.firstname.value = it },
                label = { Text("First Name") },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = lastname,
                onValueChange = { viewModel.lastname.value = it },
                label = { Text("Last Name") },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            )
        }
        OutlinedTextField(
            value = username,
            onValueChange = { viewModel.username.value = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        OutlinedTextField(
            value = mail,
            onValueChange = { viewModel.mail.value = it },
            label = { Text("Mail") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.password.value = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = password2,
            onValueChange = { viewModel.password2.value = it },
            label = { Text("Confirm password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = {
                viewModel.createAccount(login)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 33.dp, horizontal = 2.dp)

        ) {
            Text(
                text = "Create your account",
                color = Color.Black
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordPage(navigate: (String) -> Unit) {

    val viewModel = viewModel<LoginViewModel>()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(17.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Find your account",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            ),
            modifier = Modifier.padding(vertical = 130.dp)
        )

        Text(
            text = "If you have an account, you will receive a verification mail.",
            color = Color.Black,
            modifier = Modifier.padding(top = 0.dp, start = 0.dp)
        )


        var email by remember { mutableStateOf("") }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )


        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 33.dp, horizontal = 2.dp)

        ) {
            Text(
                text = "Send verification email",
                color = Color.Black
            )
        }
    }
}

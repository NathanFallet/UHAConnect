package me.nathanfallet.uhaconnect.features.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.models.UserToken
import me.nathanfallet.uhaconnect.ui.theme.darkBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
    login: (UserToken) -> Unit
) {

    val viewModel = viewModel<IdentificationViewModel>()

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
            text = stringResource(R.string.app_name),
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
            label = { stringResource(R.string.login_username) },
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
            label = { stringResource(R.string.login_password) },
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
            Text(
                text = AnnotatedString(stringResource(R.string.login_forgot_pw)),
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.clickable(onClick = { navigate("resetPasswordMail")})
            )
        }

        Button(
            onClick = {
                viewModel.login(login)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = darkBlue,
                contentColor = Color.LightGray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 33.dp, horizontal = 2.dp)
        ) {
            Text(
                text = stringResource(R.string.login_login),
            )
        }

        Row(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 40.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Text(text = stringResource(R.string.login_no_account))
            Text(
                text = stringResource(R.string.login_join_us),
                modifier = Modifier
                    .clickable(onClick = { navigate("createAccount") })
                    .padding(start = 5.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center
                )
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountPage(
    navigate: (String) -> Unit,
    login: (UserToken) -> Unit
) {

    val viewModel = viewModel<IdentificationViewModel>()

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
            text = stringResource(R.string.login_sign_up),
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
                label = { Text(stringResource(R.string.login_first_name)) },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = lastname,
                onValueChange = { viewModel.lastname.value = it },
                label = { Text(stringResource(R.string.login_last_name)) },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            )
        }
        OutlinedTextField(
            value = username,
            onValueChange = { viewModel.username.value = it },
            label = { Text(stringResource(R.string.login_username)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        OutlinedTextField(
            value = mail,
            onValueChange = { viewModel.mail.value = it },
            label = { Text(stringResource(R.string.login_email)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.password.value = it },
            label = { Text(stringResource(R.string.login_password)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = password2,
            onValueChange = { viewModel.password2.value = it },
            label = { Text(stringResource(R.string.login_confirm_pw)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = {
                viewModel.createAccount(login)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = darkBlue,
                contentColor = Color.LightGray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 33.dp, horizontal = 2.dp)

        ) {
            Text(
                text = stringResource(R.string.login_create_acc),
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetEmailPage(navigate: (String) -> Unit) {

    val viewModel = viewModel<IdentificationViewModel>()

    val mail by viewModel.mail.observeAsState("")
    val error by viewModel.error.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(17.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.login_find_acc),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            ),
            modifier = Modifier.padding(vertical = 130.dp)
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
        Text(
            text = stringResource(R.string.login_verif_email),
            modifier = Modifier.padding(top = 0.dp, start = 0.dp)
        )

        OutlinedTextField(
            value = mail,
            onValueChange = { viewModel.mail.value = it },
            label = { Text(stringResource(R.string.login_email)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )


        Button(
            onClick = {
                viewModel.sendMail(navigate)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = darkBlue,
                contentColor = Color.LightGray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 33.dp, horizontal = 2.dp)

        ) {
            Text(
                text = stringResource(R.string.login_send_verif),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordPage(navigate: (String) -> Unit) {

    val viewModel = viewModel<IdentificationViewModel>()

    val code by viewModel.code.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val password2 by viewModel.password2.observeAsState("")
    val error by viewModel.error.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(17.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.login_find_acc),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            ),
            modifier = Modifier.padding(vertical = 130.dp)
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
        Text(
            text = stringResource(R.string.login_check_email),
            modifier = Modifier.padding(top = 0.dp, start = 0.dp)
        )
        OutlinedTextField(
            value = code,
            onValueChange = { viewModel.code.value = it },
            label = { Text(stringResource(R.string.login_code)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.password.value = it },
            label = { Text(stringResource(R.string.login_new_password)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = password2,
            onValueChange = { viewModel.password2.value = it },
            label = { Text(stringResource(R.string.login_rp_new_password)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = {
                viewModel.resetPassword(navigate)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = darkBlue,
                contentColor = Color.LightGray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 33.dp, horizontal = 2.dp)

        ) {
            Text(
                text = stringResource(R.string.login_change_password),
            )
        }
    }
}

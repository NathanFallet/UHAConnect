package me.nathanfallet.uhaconnect.features.parameters

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.ui.components.UserPictureView
import me.nathanfallet.uhaconnect.ui.theme.darkBlue

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ParametersView(
    modifier: Modifier,
    token: String?,
    user: User?,
    navigate: (String) -> Unit,
    onUpdateUser: (User) -> Unit
){

    val viewModel: ParametersViewModel = viewModel()

    val newUsername by viewModel.newUsername.observeAsState("")
    val newPw by viewModel.newPassword.observeAsState("")
    val rpPw by viewModel.newPassword2.observeAsState("")

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> viewModel.selectMedia(token, user?.id, uri, context, onUpdateUser) }
    )

    LazyColumn(modifier) {
        stickyHeader {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.parameters_settings),
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigate("self_profile") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Profile"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = darkBlue,
                    titleContentColor = Color.White
                )
            )
        }
        item{
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .height(160.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(darkBlue)
                        .height(80.dp)
                        .fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .padding(vertical = 30.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box {
                        UserPictureView(
                            modifier = Modifier
                                .clickable {
                                    imagePickerLauncher.launch("image/*")
                                },
                            user = user,
                            size = 100.dp
                        )
                        Icon(
                            painter = painterResource(R.drawable.edit),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 70.dp)
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            tint = Color.Black
                        )
                    }
                }
            }
            Column (modifier = Modifier
                .padding(start = 16.dp, bottom = 8.dp, end = 16.dp)
                .fillMaxWidth())
            {
                Text(text = stringResource(R.string.parameters_change_un))
                OutlinedTextField(
                    value = newUsername,
                    onValueChange = { viewModel.newUsername.value = it },
                    label = { stringResource(R.string.login_username) },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    keyboardActions = KeyboardActions(),
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Default,
                        fontSize = 14.sp
                    ),
                    singleLine = true,
                    maxLines = 1
                )
                Button(onClick = {
                    viewModel.changeUsername(token, user?.id, onUpdateUser)
                }) {
                    Text(stringResource(R.string.parameters_validate))
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Validate",
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
                Divider(modifier = Modifier.padding(bottom = 8.dp, top = 8.dp))
                Text(text = stringResource(R.string.parameters_change_password))
                OutlinedTextField(
                    value = newPw,
                    onValueChange = { viewModel.newPassword.value = it },
                    label = { stringResource(R.string.parameters_new_password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .padding(top = 0.dp, bottom = 0.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                OutlinedTextField(
                    value = rpPw,
                    onValueChange = { viewModel.newPassword2.value = it },
                    label = { stringResource(R.string.parameters_repeat_password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .padding(top = 0.dp, bottom = 0.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                Button(onClick = {
                    viewModel.changePassword(token, user?.id, onUpdateUser)
                }) {
                    Text(stringResource(R.string.parameters_validate))
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Validate",
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
            }
        }
    }
}
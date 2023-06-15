package me.nathanfallet.uhaconnect.features.compose

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.ui.components.UserPictureView
import me.nathanfallet.uhaconnect.ui.theme.darkBlue


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ComposeView(
    modifier: Modifier,
    navigate: (String) -> Unit,
    token: String?,
    viewedBy: User?
) {

    val viewModel = viewModel<ComposeViewModel>()

    val postContent by viewModel.postContent.observeAsState()
    val titleContent by viewModel.titleContent.observeAsState()
    val tags by viewModel.tags.observeAsState()
    val newTag by viewModel.newTag.observeAsState()

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> viewModel.selectMedia(token, uri, context) }
    )
    var showDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        stickyHeader{
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title_activity_new_post),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigate("feed")
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Home"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = darkBlue,
                    titleContentColor = Color.White
                )
            )
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically

            ) {
                UserPictureView(
                    user = viewedBy,
                    size = 40.dp
                )
                Button(
                    onClick = {
                        showDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkBlue,
                        contentColor = Color.LightGray
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = stringResource(R.string.compose_tag), color = Color.White)
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Tag",
                        tint = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                if (showDialog) {
                    Dialog(
                        onDismissRequest = { showDialog = false }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 30.dp, vertical = 40.dp)
                            ) {
                                OutlinedTextField(
                                    value = newTag ?: "",
                                    onValueChange = { viewModel.newTag.value = it },
                                )
                                Button(
                                    onClick = {
                                        viewModel.addTag()
                                        showDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = darkBlue,
                                        contentColor = Color.LightGray
                                    ),
                                    enabled = newTag?.isNotBlank() == true,
                                ) {
                                    Text(text = stringResource(R.string.compose_add_tag))
                                }
                            }
                        }
                    }
                }
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkBlue,
                        contentColor = Color.LightGray
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = stringResource(R.string.compose_image), color = Color.White)
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Image",
                        tint = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
        item {
            TextField(
                value = titleContent ?: "",
                onValueChange = { viewModel.titleContent.value = it },
                label = { Text(stringResource(R.string.compose_title)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
                    .height(50.dp)
            )
        }
        item {
            TextField(
                value = postContent ?: "",
                onValueChange = { viewModel.postContent.value = it },
                label = { Text(stringResource(R.string.compose_mind)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
                    .height(300.dp)
            )
        }
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                tags?.forEach {
                    Box(
                        modifier = Modifier
                            .background(Color.Yellow, RoundedCornerShape(20.dp))
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier
                                .padding(8.dp),
                            color = Color.DarkGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        item {
            Button(
                onClick = {
                    viewModel.post(token, navigate)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = darkBlue,
                    contentColor = Color.LightGray
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            ) {
                Text(text = stringResource(R.string.compose_post), color = Color.White)
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Post",
                    tint = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

package me.nathanfallet.uhaconnect.features.compose

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.extensions.pictureUrl
import me.nathanfallet.uhaconnect.models.User
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

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> viewModel.selectMedia(token, uri, context) }
    )

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
                AsyncImage(
                    model = viewedBy?.pictureUrl,
                    contentDescription = viewedBy?.username,
                    placeholder = painterResource(id = R.drawable.picture_placeholder),
                    error = painterResource(id = R.drawable.picture_placeholder),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(
                            BorderStroke(1.dp, Color.White),
                            CircleShape
                        )
                )
                Button(
                    onClick = {
                        // TODO: Add tag
                    }, modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = stringResource(R.string.compose_tag), color = Color.White)
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Post",
                        tint = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Button(
                    onClick = { imagePickerLauncher.launch("image/*, video/*") },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.round_file_present_24),
                        contentDescription = "My Icon"
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
            Button(
                onClick = {
                    viewModel.post(token, navigate)
                },
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

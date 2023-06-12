package me.nathanfallet.uhaconnect.features.compose

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.ui.theme.darkBlue


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ComposeView(
    modifier: Modifier, token: String?, navigate: (String) -> Unit
) {

    val viewModel = viewModel<ComposeViewModel>()
    val context = LocalContext.current

    val postContent by viewModel.postContent.observeAsState("")

    val titleContent by viewModel.titleContent.observeAsState("")
    val id by viewModel.id.observeAsState()

    val activity = LocalContext.current as? Activity
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (token != null) {
                if (uri != null) {
                    viewModel.selectMedia(token, uri, context)
                }
            }
        }
    )


    if (id != null) navigate("post/$id")

    LazyColumn(
        modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        stickyHeader{
            TopAppBar(title = {
                Text(
                    text = stringResource(R.string.app_name),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )},
                colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = darkBlue,
                titleContentColor = Color.White
            )
            )
        }

        /*Buttons and profile photo*/
        item{
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .size(75.dp)
                        .clip(CircleShape)
                        .border(
                            BorderStroke(3.dp, Color.White), CircleShape
                        )
                )
                Button(
                    onClick = {
                        viewModel.post(token)
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
                    onClick = {imagePickerLauncher.launch("image/*, video/*")}, modifier = Modifier.padding(start = 8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.round_file_present_24),
                        contentDescription = "My Icon"
                    )

                }
            }

            /*Text fields for posting*/
            TextField(
                value = titleContent,
                onValueChange = { viewModel.titleContent.value = it },
                label = { Text(stringResource(R.string.compose_title)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .padding(vertical = 8.dp)
                    .height(50.dp)
            )

            TextField(
                value = postContent,
                onValueChange = { viewModel.postContent.value = it },
                label = { Text(stringResource(R.string.compose_mind)) },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .padding(vertical = 8.dp)
                    .height(300.dp)

            )
            Button(
                onClick = {
                    viewModel.post(token)
                }, modifier = Modifier.padding(start = 8.dp)
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

package me.nathanfallet.uhaconnect.features.post

import me.nathanfallet.uhaconnect.features.post.PostViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.nathanfallet.uhaconnect.models.Post
import me.nathanfallet.uhaconnect.ui.theme.darkBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostView(modifier: Modifier, viewModel: PostViewModel) {
    val post by viewModel.post.observeAsState()
    val comments by viewModel.comments.observeAsState()
    val user by viewModel.user.observeAsState()

    Surface(
        modifier = modifier,
        color = Color.LightGray
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            TopAppBar(
                title = {
                    Text(
                        text = "UHAConnect",
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = darkBlue,
                    titleContentColor = Color.White
                )            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(
                        border = BorderStroke(1.dp, Color.Black),
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    post?.let { post ->
                        Text(
                            text = post.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Text(
                            text = "Author :  ${post.user_id}, date : ${post.date}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Text(
                            text = post.content,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                comments?.forEach { comment ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(color = Color.White, shape = MaterialTheme.shapes.medium)
                    ) {
                        Column {
                            Text(
                                text = comment.id_user.toString(),
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Text(
                                text = comment.content,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Black,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 8.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                TextField(
                    value = TextFieldValue(""),
                    onValueChange = {},
                    label = { Text("Write a comment") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    val viewModel = viewModel<PostViewModel>()
    PostView(modifier = Modifier.fillMaxSize(), viewModel = viewModel)
}

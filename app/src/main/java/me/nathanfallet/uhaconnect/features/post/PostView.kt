package me.nathanfallet.uhaconnect.features.post

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.lifecycle.viewmodel.compose.viewModel
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.models.RoleStatus
import me.nathanfallet.uhaconnect.ui.theme.darkBlue


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PostView(modifier: Modifier, navigate: (String)->Unit, token: String?) {

    val viewModel: PostViewModel = viewModel()

    val newComment by viewModel.newComment.observeAsState("")

    val post by viewModel.post.observeAsState()
    val comments by viewModel.comments.observeAsState(listOf())
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    if (post == null) viewModel.loadData(token)

    LazyColumn(modifier){
        stickyHeader{
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigate("feed") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    post?.let { post ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ){
                            Text(
                                text = post.title,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ){
                            Text(
                                text = stringResource(R.string.post_author, post.user?.username.toString()),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Text(
                                text = stringResource(R.string.post_date, post.date.toString()),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        Text(
                            text = post.content,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically

            ) {
                TextField(
                    value = newComment,
                    onValueChange = {viewModel.newComment.value = it},
                    label = { Text(stringResource(R.string.post_write_comment)) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                IconButton(onClick = { viewModel.sendComment(token) }) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                    )
                }
            }
        }
        items(comments){comment ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column {
                    Row(horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()){
                        Text(
                            text = comment.user?.username.toString(),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        // TODO: Use logged user instead
                        if (post?.user?.role == RoleStatus.ADMINISTRATOR) {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentSize(Alignment.TopEnd)
                            ) {
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "More"
                                    )
                                }

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Remove comment") },
                                        onClick = {
                                            Toast.makeText(
                                                context,
                                                "Comment has been removed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Ban user") },
                                        onClick = {
                                            Toast.makeText(
                                                context,
                                                "User has been banned",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            }
                        }


                    }
                    Text(
                        text = comment.content,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(
                            start = 8.dp,
                            top = 4.dp,
                            end = 8.dp,
                            bottom = 8.dp
                        )
                    )
                }
            }
        }
    }
}

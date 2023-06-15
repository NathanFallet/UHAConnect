package me.nathanfallet.uhaconnect.features.post

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.models.RoleStatus
import me.nathanfallet.uhaconnect.models.UpdateUserPayload
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.ui.components.CommentCard
import me.nathanfallet.uhaconnect.ui.components.PostCard
import me.nathanfallet.uhaconnect.ui.theme.darkBlue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PostView(
    modifier: Modifier,
    navigate: (String) -> Unit,
    token: String?,
    viewedBy: User?
) {

    val viewModel: PostViewModel = viewModel()

    val newComment by viewModel.newComment.observeAsState()
    val post by viewModel.post.observeAsState()
    val comments by viewModel.comments.observeAsState()
    val hasMore by viewModel.hasMore.observeAsState()

    if (post == null) viewModel.loadPost(token)
    else if (comments == null) viewModel.loadComments(token, true)

    LazyColumn(modifier) {
        stickyHeader {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title_activity_post_view),
                        color = Color.White,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigate("feed") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = darkBlue,
                    titleContentColor = Color.White
                )
            )
        }
        post?.let { post ->
            item {
                PostCard(
                    modifier = Modifier
                        .padding(16.dp),
                    post = post,
                    navigate = navigate,
                    favoriteCheck = {
                        viewModel.favoritesHandle(token, post.id, it)
                    },
                    updatePost = {
                        viewModel.updatePost(token, post.id, it)
                    },
                    deletePost = {
                        viewModel.deletePost(token, post.id)
                    },
                    updateUser = {
                        viewModel.updateUser(token, post.user_id, it)
                    },
                    viewedBy = viewedBy,
                    detailed = true
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = newComment ?: "",
                        onValueChange = { viewModel.newComment.value = it },
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
            items(comments ?: listOf()) { comment ->
                CommentCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 8.dp),
                    comment = comment,
                    deleteComment = {
                        viewModel.deleteComment(token, post.id, comment.id)
                    },
                    updateUser ={
                        viewModel.updateUser(token, comment.user_id, UpdateUserPayload(role = RoleStatus.BANNED))
                    },
                    viewedBy = viewedBy,
                    navigate = navigate
                )
                if (hasMore == true && comments?.lastOrNull()?.id == comment.id) {
                    // Load more comments (pagination)
                    viewModel.loadComments(token, false)
                }
            }
        }
    }
}

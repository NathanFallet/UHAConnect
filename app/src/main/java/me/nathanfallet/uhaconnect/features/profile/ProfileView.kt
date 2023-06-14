package me.nathanfallet.uhaconnect.features.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.extensions.text
import me.nathanfallet.uhaconnect.models.RoleStatus
import me.nathanfallet.uhaconnect.models.UpdateUserPayload
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.ui.components.PostCard
import me.nathanfallet.uhaconnect.ui.components.UserPictureView
import me.nathanfallet.uhaconnect.ui.theme.darkBlue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProfileView(
    modifier: Modifier,
    navigate: (String) -> Unit,
    token: String?,
    disconnect: () -> Unit,
    viewedBy: User?,
    onUpdateUser: (User) -> Unit,

) {

    val viewModel: ProfileViewModel = viewModel()

    val posts by viewModel.posts.observeAsState()
    val user by viewModel.user.observeAsState()
    val hasMore by viewModel.hasMore.observeAsState()

    if (user == null) viewModel.loadUser(token)
    else if (posts == null) viewModel.loadPosts(token, true)

    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    LazyColumn(modifier) {
        stickyHeader {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profile_profile),
                        color = Color.White
                    )
                },
                actions = {
                    if (user?.id == viewedBy?.id){
                        IconButton(onClick = disconnect) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_logout_24),
                                contentDescription = "Logout"
                            )
                        }
                        IconButton(onClick = {
                            navigate("settings")
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_settings_24),
                                contentDescription = "Settings"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = darkBlue,
                    titleContentColor = Color.White
                )
            )
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .height(116.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(darkBlue)
                        .height(54.dp)
                        .fillMaxWidth()
                )
                Row(modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)) {
                    UserPictureView(
                        user = user,
                        size = 100.dp
                    )
                    Column(modifier = Modifier.padding(top = 14.dp)) {
                        Text(
                            text = "${user?.firstName} ${user?.lastName}",
                            fontSize = 24.sp,
                            color = Color.Yellow,
                            modifier = Modifier.padding(start = 15.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = user?.username ?: "",
                            modifier = Modifier.padding(start = 15.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        user?.role?.let {
                            Text(
                                text = stringResource(id = it.text),
                                modifier = Modifier.padding(start = 15.dp)
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = stringResource(R.string.profile_followers_list),
                    modifier = Modifier
                        .clickable(onClick = { navigate("follows/${user?.id}/followers") })
                        .padding(start = 16.dp)
                )
                Text(
                    text = stringResource(R.string.profile_following_list),
                    modifier = Modifier
                        .clickable(onClick = { navigate("follows/${user?.id}/following") })
                        .padding(start = 16.dp)
                )
                if (user?.id != viewedBy?.id) {
                    Button(
                        onClick = {viewModel.followHandle(token, user?.id, user?.follow != null) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (user?.follow != null) darkBlue else Color.LightGray,
                            contentColor = if (user?.follow != null) Color.LightGray else darkBlue
                        ),
                        modifier = Modifier
                            .padding(top = 5.dp, start = 8.dp)
                    ) {
                        Text(stringResource(
                            id = if (user?.follow != null) R.string.profile_following else R.string.profile_follow)
                        )
                    }
                }
                //TODO: use current user instead
                if (viewedBy?.role == RoleStatus.ADMINISTRATOR) {
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
                                text = { Text(stringResource(R.string.profile_ban)) },
                                onClick = {
                                    viewModel.updateUser(token, user!!.id, UpdateUserPayload(role = RoleStatus.BANNED))
                                }
                            )
                        }
                    }
                }
            }
        }
        items(posts ?: listOf()) { post ->
            PostCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 8.dp),
                post,
                navigate,
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
                viewedBy = viewedBy
            )
            if (hasMore == true && posts?.lastOrNull()?.id == post.id) {
                // Load more posts (pagination)
                viewModel.loadPosts(token, false)
            }
        }
    }
}
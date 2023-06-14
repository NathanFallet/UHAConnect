package me.nathanfallet.uhaconnect.features.feed

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.models.Permission
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.ui.components.PostCard
import me.nathanfallet.uhaconnect.ui.theme.darkBlue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FeedView(
    modifier: Modifier,
    navigate: (String) -> Unit,
    token: String?,
    user: User?,
) {

    val viewModel: FeedViewModel = viewModel()

    val posts by viewModel.posts.observeAsState()
    val hasMore by viewModel.hasMore.observeAsState()

    if (posts == null) viewModel.loadData(token, true)

    var following by remember { mutableStateOf(false) }


    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        stickyHeader{
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            when (viewModel.loader) {
                                "favorites" -> R.string.title_activity_favs_view
                                "validation" -> R.string.title_activity_validation_view
                                else -> R.string.app_name
                            }
                        ),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    if (viewModel.loader !in listOf("posts", "following")) {
                        IconButton(onClick = {
                            navigate("feed")
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Home"
                            )
                        }
                    }
                },
                actions = {
                    if (viewModel.loader in listOf("posts", "following")) {
                        IconButton(onClick = {
                            navigate("feed/compose")
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.post_add),
                                contentDescription = "New post"
                            )
                        }
                        IconButton(onClick = {
                            navigate("feed/favorites")
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.favorite),
                                contentDescription = "Favorites"
                            )
                        }
                        if (user?.role?.hasPermission(Permission.POST_UPDATE) == true) {
                            IconButton(onClick = {
                                navigate("feed/validation")
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_drafts_24),
                                    contentDescription = "Requests"
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = darkBlue,
                    titleContentColor = Color.White
                )
            )}
        item {
            if (viewModel.loader in listOf("posts", "following")){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.feed_following),
                        fontSize = 20.sp,
                        fontWeight = if (viewModel.loader == "following") FontWeight.Bold
                        else FontWeight.Thin,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .clickable(onClick = {
                                navigate("feed/following")
                            })
                    )
                    Divider(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .fillMaxHeight()
                            .width(2.dp)
                            .height(20.dp)
                    )
                    Text(
                        stringResource(R.string.feed_all),
                        fontSize = 20.sp,
                        fontWeight = if (viewModel.loader != "following") FontWeight.Bold
                        else FontWeight.Thin,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .clickable(onClick = {
                                navigate("feed/posts")
                                following = !following
                            })
                    )
                }
            }
        }
        items(posts ?: listOf()) { post ->
            PostCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
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
                updateUser =  {
                    viewModel.updateUser(token, post.user_id, it)
                },
                viewedBy = user
            )
            if (hasMore == true && posts?.lastOrNull()?.id == post.id) {
                // Load more posts (pagination)
                viewModel.loadData(token, false)
            }
        }
    }
}

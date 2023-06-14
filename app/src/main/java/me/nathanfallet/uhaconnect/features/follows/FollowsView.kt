package me.nathanfallet.uhaconnect.features.follows

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.ui.theme.darkBlue
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import me.nathanfallet.uhaconnect.extensions.pictureUrl
import me.nathanfallet.uhaconnect.extensions.text
import me.nathanfallet.uhaconnect.ui.components.UserPictureView


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FollowsView(
    modifier: Modifier,
    token: String?,
    navigate: (String) -> Unit
){

    val viewModel: FollowsViewModel = viewModel()

    val follows by viewModel.follows.observeAsState()
    val user by viewModel.user.observeAsState()
    val hasMore by viewModel.hasMore.observeAsState()

    if (user == null) viewModel.loadUser(token)
    else if (follows == null) viewModel.loadFollows(token, true)

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        stickyHeader {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            id = if (viewModel.loader == "followers") R.string.title_activity_followers
                            else R.string.title_activity_following,
                            user?.username ?: ""),
                        color = Color.White,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigate("profile/${user?.id}") }) {
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
        items(follows ?: listOf()){follow ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            navigate("profile/${follow.id}")
                        }
                ) {
                    UserPictureView(
                        user = follow,
                        size = 22.dp
                    )
                    Text(
                        text = follow.username,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            if (hasMore == true && follows?.lastOrNull()?.id == follow.id) {
                // Load more follows (pagination)
                viewModel.loadFollows(token, false)
            }
        }

    }
}
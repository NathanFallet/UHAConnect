package me.nathanfallet.uhaconnect.features.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.extensions.pictureUrl
import me.nathanfallet.uhaconnect.models.RoleStatus
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.ui.components.PostCard
import me.nathanfallet.uhaconnect.ui.theme.darkBlue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProfileView(
    modifier: Modifier,
    navigate: (String) -> Unit,
    token: String?,
    disconnect: () -> Unit,
    viewedBy: User?
) {

    val viewModel: ProfileViewModel = viewModel()

    val posts by viewModel.posts.observeAsState()
    val user by viewModel.user.observeAsState()

    if (posts == null || user == null) viewModel.loadData(token)

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
                    .height(160.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(darkBlue)
                        .height(80.dp)
                        .fillMaxWidth()
                )
                Row(modifier = Modifier.padding(vertical = 30.dp, horizontal = 16.dp)) {
                    AsyncImage(
                        model = user?.pictureUrl,
                        contentDescription = user?.username,
                        placeholder = painterResource(id = R.drawable.picture_placeholder),
                        error = painterResource(id = R.drawable.picture_placeholder),
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(
                                BorderStroke(3.dp, Color.White),
                                CircleShape
                            )
                    )
                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                        Text(
                            text = user?.username ?: "",
                            fontSize = 30.sp,
                            color = Color.Yellow,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        Text(
                            text = user?.role.toString(),
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        //TODO: use current user instead
                        if (user?.role == RoleStatus.ADMINISTRATOR) {
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
                                        text = { Text("Ban user") },
                                        onClick = { Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show() }
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
        items(posts ?: listOf()) { post ->
            PostCard(
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
                updateUser ={

                },
                viewedBy = viewedBy
            )
        }
    }
}
package me.nathanfallet.uhaconnect.ui.components


import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jeziellago.compose.markdowntext.MarkdownText
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.extensions.timeAgo
import me.nathanfallet.uhaconnect.models.Permission
import me.nathanfallet.uhaconnect.models.Post
import me.nathanfallet.uhaconnect.models.RoleStatus
import me.nathanfallet.uhaconnect.models.UpdatePostPayload
import me.nathanfallet.uhaconnect.models.UpdateUserPayload
import me.nathanfallet.uhaconnect.models.User


@Composable
fun PostCard(
    modifier: Modifier = Modifier,
    post: Post,
    navigate: (String) -> Unit,
    favoriteCheck: (Boolean) -> Unit,
    updatePost: (UpdatePostPayload) -> Unit,
    deletePost: () -> Unit,
    updateUser: (UpdateUserPayload) -> Unit,
    viewedBy: User?,
    detailed: Boolean = false
){
  
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Card(modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable {
                        navigate("profile/${post.user?.id}")
                    }
                ) {
                    UserPictureView(
                        user = post.user,
                        size = 40.dp
                    )
                    Column {
                        Text("${post.user?.firstName} ${post.user?.lastName}")
                        Text(
                            text = post.user?.username ?: "",
                            fontSize = 12.sp,
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.postcard_ago, post.date.timeAgo(context)),
                    fontSize = 12.sp
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = post.title,
                    modifier = Modifier.padding(bottom = 5.dp, top = 5.dp),
                    fontSize = 24.sp,
                )
                if (viewedBy?.role?.hasPermission(Permission.POST_DELETE) == true) {

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
                                text = { Text("Remove post") },
                                onClick = deletePost
                            )
                            if (!post.validated) {
                                DropdownMenuItem(
                                    text = { Text("Validate post") },
                                    onClick = {
                                        updatePost(UpdatePostPayload(null, null, true))
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Ban user") },
                                onClick = {
                                    updateUser(UpdateUserPayload(role = RoleStatus.BANNED))
                                }
                            )
                        }
                    }
                }
            }
            MarkdownText(
                modifier = modifier.fillMaxWidth(),
                markdown = post.content,
                color = Color.White,
                textAlign = TextAlign.Start
            )
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                IconButton(onClick = { favoriteCheck(post.favorite != null) }) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Like",
                        tint = if (post.favorite == null) Color.Black else Color.White
                    )
                }
                if (!detailed) {
                    Button(
                        onClick = { navigate("post/${post.id}") },
                        //colors = ButtonDefaults.buttonColors(backgroundColor = darkBlue),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(text = stringResource(R.string.postcard_showmore))
                    }
                }
            }
        }
    }
}

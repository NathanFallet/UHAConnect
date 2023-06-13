package me.nathanfallet.uhaconnect.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.extensions.pictureUrl
import me.nathanfallet.uhaconnect.models.Comment
import me.nathanfallet.uhaconnect.models.Permission
import me.nathanfallet.uhaconnect.models.User

@Composable
fun CommentCard(
    modifier: Modifier = Modifier,
    comment: Comment,
    deleteComment: () -> Unit,
    viewedBy: User?
) {

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = comment.user?.pictureUrl,
                    contentDescription = comment.user?.username,
                    placeholder = painterResource(id = R.drawable.picture_placeholder),
                    error = painterResource(id = R.drawable.picture_placeholder),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .border(
                            BorderStroke(1.dp, Color.White),
                            CircleShape
                        )
                )
                Text(
                    text = comment.user?.username.toString(),
                    style = MaterialTheme.typography.titleSmall
                )
                if (viewedBy?.role?.hasPermission(Permission.COMMENT_DELETE) == true) {
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
                                onClick = deleteComment
                            )
                            DropdownMenuItem(
                                text = { Text("Ban user") },
                                onClick = {
                                    //TODO : Ban user method
                                }
                            )
                        }
                    }
                }
            }
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

}
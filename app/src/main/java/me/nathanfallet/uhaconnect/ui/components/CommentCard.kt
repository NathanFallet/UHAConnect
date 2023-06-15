package me.nathanfallet.uhaconnect.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.extensions.text
import me.nathanfallet.uhaconnect.models.Comment
import me.nathanfallet.uhaconnect.models.Permission
import me.nathanfallet.uhaconnect.models.RoleStatus
import me.nathanfallet.uhaconnect.models.UpdateUserPayload
import me.nathanfallet.uhaconnect.models.User

@Composable
fun CommentCard(
    modifier: Modifier = Modifier,
    comment: Comment,
    deleteComment: () -> Unit,
    updateUser: (UpdateUserPayload) -> Unit,
    viewedBy: User?,
    navigate: (String)->Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clickable { navigate("profile/${comment.user?.id}") }
                        .weight(1f)
                ) {
                    UserPictureView(
                        user = comment.user,
                        size = 22.dp
                    )
                    Text(
                        text = comment.user?.username.toString(),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                if (viewedBy?.role?.hasPermission(Permission.COMMENT_DELETE) == true) {
                    Box(
                        modifier = Modifier
                            .wrapContentSize(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            modifier = Modifier.clickable { expanded = !expanded }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.post_card_remove_comment)) },
                                onClick = deleteComment
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.post_card_ban)) },
                                onClick = {
                                    updateUser(UpdateUserPayload(role = RoleStatus.BANNED))
                                    expanded = false
                                }
                            )
                            if (viewedBy.role == RoleStatus.ADMINISTRATOR) {
                                Picker(
                                    items = mapOf(
                                        RoleStatus.STUDENT to stringResource(RoleStatus.STUDENT.text),
                                        RoleStatus.TEACHER to stringResource(RoleStatus.TEACHER.text),
                                        RoleStatus.STAFF to stringResource(RoleStatus.STAFF.text),
                                        RoleStatus.MODERATOR to stringResource(RoleStatus.MODERATOR.text),
                                        RoleStatus.ADMINISTRATOR to stringResource(RoleStatus.ADMINISTRATOR.text)
                                    ),
                                    placeholder = stringResource(R.string.role_set),
                                    onSelected = {
                                        updateUser(UpdateUserPayload(role = it))
                                        expanded = false
                                    }
                                )
                            }
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
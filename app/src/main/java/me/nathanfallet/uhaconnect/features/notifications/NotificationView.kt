package me.nathanfallet.uhaconnect.features.notifications

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import me.nathanfallet.uhaconnect.extensions.text
import me.nathanfallet.uhaconnect.ui.components.UserPictureView
import me.nathanfallet.uhaconnect.ui.theme.darkBlue


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotificationView(
    modifier: Modifier,
    token: String?,
    navigate: (String)->Unit
) {

    val viewModel = viewModel<NotificationViewModel>()
    val notifications by viewModel.notifications.observeAsState()
    val hasMore by viewModel.hasMore.observeAsState()

    if (notifications == null) viewModel.loadData(token, true)

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        stickyHeader {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title_activity_notification),
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = darkBlue,
                    titleContentColor = Color.White
                )
            )
        }
        items(notifications ?: listOf()) { notification ->
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
                            navigate("profile/${notification.user?.id}")
                        }
                ) {
                    UserPictureView(
                        user = notification.user,
                        size = 22.dp
                    )
                    Text(
                        text = stringResource(
                            id = notification.type.text,
                            notification.user?.username ?: ""
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            if (hasMore == true && notifications?.lastOrNull()?.id == notification.id) {
                // Load more notifications (pagination)
                viewModel.loadData(token, false)
            }
        }
    }
}


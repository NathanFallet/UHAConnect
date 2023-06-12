package me.nathanfallet.uhaconnect.features.notifications

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import me.nathanfallet.uhaconnect.ui.theme.darkBlue


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotificationView(
    modifier: Modifier,
    token: String?
) {

    val viewModel = viewModel<NotificationViewModel>()
    val notifications by viewModel.notifications.observeAsState()

    if (notifications == null) viewModel.loadData(token)

    LazyColumn(modifier){
        stickyHeader {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name),
                    color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
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
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            id = notification.type.text(),
                            notification.user?.username ?: ""
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}


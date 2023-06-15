package me.nathanfallet.uhaconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun <T> Picker(
    items: Map<T, String>,
    placeholder: String = "",
    onSelected: (T) -> Unit
) {

    var showDialog by remember { mutableStateOf(false) }

    DropdownMenuItem(
        text = { Text(placeholder) },
        onClick = {
            showDialog = true
        }
    )
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 40.dp)
                    .background(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface
                    )
            ) {
                items(items.toList()) { item ->
                    Text(
                        text = item.second,
                        modifier = Modifier
                            .clickable {
                                onSelected(item.first)
                                showDialog = false
                            }
                            .fillMaxWidth()
                            .padding(20.dp)
                    )
                }
            }
        }
    }

}
package me.nathanfallet.uhaconnect.features.Favs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import me.nathanfallet.uhaconnect.features.post.PostView
import me.nathanfallet.uhaconnect.ui.theme.darkBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavsView(modifier: Modifier, viewModel: FavsViewModel) {
    Surface(
        modifier,
        color = Color.LightGray
        ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "UHAConnect",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = darkBlue,
                    titleContentColor = Color.White
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = { /* Handle search query change */ },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "Search",
                            style = MaterialTheme.typography.bodyMedium,
                            //color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = Color.Black
                            )
                        }
                    },
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(vertical = 8.dp)
                        .border(
                            border = BorderStroke(1.dp, Color.Black),
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "Titre ",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Text(
                            text = "Publié par xxx, le jj.mm.annee",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Text(
                            text = "Lorem Elsass ipsum Heineken siuglopf aabitant ornare geht's quam. réchime eget météor hopla id, gewurztraminer schneck Racing. Kabinetpapier turpis...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(
                                onClick = {},
                                //colors = ButtonDefaults.buttonColors(backgroundColor = darkBlue),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(text = "Show more", color = Color.White)
                            }
                        }

                    }
                }

            }
        }
        }
    }


@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    val viewModel = viewModel<FavsViewModel>()
    FavsView(Modifier.fillMaxSize(), viewModel)
}

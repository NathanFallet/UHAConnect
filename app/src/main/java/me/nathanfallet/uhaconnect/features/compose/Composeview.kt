package me.nathanfallet.uhaconnect.features.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.ui.theme.darkBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeView(modifier: Modifier,
                token : String?,
                navigate : (String) -> Unit
    ) {

    val viewModel = viewModel<ComposeViewModel>()

    val postContent by viewModel.postContent.observeAsState("")

    val titleContent by viewModel.titleContent.observeAsState("")
    val id by viewModel.id.observeAsState()

    if (id != null) navigate("post/$id")

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

            /*Buttons and profile photo*/
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .size(75.dp)
                        .clip(CircleShape)
                        .border(
                            BorderStroke(3.dp, Color.White),
                            CircleShape
                        )
                    )

                Button(
                    onClick = {},
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = "Add tag", color = Color.White)
                }
                Button(
                    onClick = {
                              viewModel.post(token)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = "Post", color = Color.White)
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Post",
                        tint = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Button(
                    onClick = {},
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = "File", color = Color.White)
                }



            }

            /*Text fields for posting*/
            TextField(
                value = titleContent,
                onValueChange = { viewModel.titleContent.value = it },
                label = { Text("Title") },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .padding(vertical = 8.dp)
                    .height(50.dp)
            )

            TextField(
                value = postContent,
                onValueChange = { viewModel.postContent.value = it },
                label = { Text("What's in your mind ?") },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .padding(vertical = 8.dp)
                    .height(300.dp)

            )


        }

    }

}
@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    ComposeView(Modifier.fillMaxSize(),
        token = "", {}
    )
}

package me.nathanfallet.uhaconnect.ui.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.extensions.timeAgo
import me.nathanfallet.uhaconnect.models.Post


@Composable
fun PostCard(post: Post, navigate: (String)->Unit){

    val context = LocalContext.current

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ){
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
        ){
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()){
                Text(text = post.user?.username ?: "")
                Text(text = stringResource(R.string.postcard_ago, post.date.timeAgo(context)), fontSize = 12.sp)
            }
            Text(text = post.title,
                modifier = Modifier.padding(bottom = 5.dp, top = 5.dp),
                fontSize = 24.sp,
            )
            Text(text = post.content)
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp),
                horizontalArrangement = Arrangement.End
            ){
                Button(
                    onClick = { navigate("post") },
                    //colors = ButtonDefaults.buttonColors(backgroundColor = darkBlue),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = stringResource(R.string.postcard_showmore))
                }
            }
        }
    }
}

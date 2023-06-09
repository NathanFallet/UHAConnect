package me.nathanfallet.uhaconnect.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.extensions.timeAgo
import me.nathanfallet.uhaconnect.models.Post


@Composable
fun PostCard(post: Post, navigate: (String)->Unit){

    val context = LocalContext.current

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .background(Color(0xFFE8E8E8))
        .border(
            border = BorderStroke(1.dp, Color.Black),
            shape = MaterialTheme.shapes.medium
        )
        .size(200.dp)
    ){
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
        ){
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()){
                Text(text = post.user?.username ?: "")
                Text(text = stringResource(R.string.postcard_ago, post.date.timeAgo(context)), fontSize = 12.sp, color = Color.DarkGray)
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
                    Text(text = stringResource(R.string.postcard_showmore), color = Color.White)
                }
            }
        }
    }
}

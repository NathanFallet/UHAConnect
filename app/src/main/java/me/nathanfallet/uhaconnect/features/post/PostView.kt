import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import me.nathanfallet.uhaconnect.ui.theme.darkBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostView() {

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
                        IconButton(onClick = { /* Action du profil */ }) {
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

                Text(
                    text = "Titre article",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    text = "Publié par xxx , le jj.mm.annee",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    text = "Text de test Text de test Text de test Text de test Text de " +
                            "test Text de test Text de test Text de test Text de testText de " +
                            "testText de testText de testText de testText de testText de testText de testText de testText de testText de testText de testText de testText de testText de testText de test",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .padding(horizontal = 8.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = Color.Black,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .padding(horizontal = 4.dp)

                    )
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Favorite",
                        tint = Color.Black,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .padding(horizontal = 4.dp)

                    )
                    Icon(
                        Icons.Outlined.Create,
                        contentDescription = "Comment",
                        tint = Color.Black,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .padding(horizontal = 4.dp)

                    )
                }


            }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    PostView()
}

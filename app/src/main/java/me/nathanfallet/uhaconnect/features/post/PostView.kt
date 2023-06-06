import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import me.nathanfallet.uhaconnect.ui.theme.darkBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostView() {
    Surface(color = Color.LightGray) {
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
                        text = "Titre article",
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
                        text = "Lorem Elsass ipsum Heineken sit consectetur elit dolor Spätzle ullamcorper schpeck knack dui hoplageiss et turpis, Morbi hop tellus Pfourtz ! Chulia Roberstau tellus Salut bisamme Wurschtsalad pellentesque Gal ! Miss Dahlias Hans non risus, Verdammi DNA, eleifend varius lacus Yo dû. Strasbourg sit vulputate Carola lotto-owe id leo placerat kuglopf auctor, aliquam non knepfle sit schnaps yeuh. ac amet ch'ai sed Pellentesque kartoffelsalad wurscht blottkopf, Huguette wie libero. in, hopla Gal. rucksack hopla gal quam, baeckeoffe nullam Christkindelsmärik leverwurscht bredele ac libero, semper messti de Bischheim tristique senectus elementum commodo Mauris Salu bissame sagittis merci vielmols so morbi condimentum tchao bissame Oberschaeffolsheim mamsell und rhoncus flammekueche amet munster gravida Coopé de Truchtersheim vielmols, adipiscing nüdle barapli geïz suspendisse hopla ante Chulien dignissim Oberschaeffolsheim amet, picon bière mollis libero, salu ftomi! rossbolla porta kougelhopf mänele purus leo s'guelt bissame sed ornare jetz gehts los chambon Richard Schirmeck habitant ornare geht's quam. réchime eget météor hopla id, gewurztraminer schneck Racing. Kabinetpapier turpis",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
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
                Icon(
                    Icons.Filled.Share,
                    contentDescription = "Share",
                    tint = Color.Black,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .padding(horizontal = 4.dp)
                )
            }

            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Commentaires",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(vertical = 8.dp)
                    .background(Color.White, shape = MaterialTheme.shapes.medium)

            ) {
                Column(
                    modifier = Modifier.padding(2.dp)
                ) {
                    Text(
                        text = "Anonyme",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .padding(horizontal = 2.dp)
                    )
                    Text(
                        text = "Lorem Elsass ipsum Heineken sit consectetur elit dolor Spätzle",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .padding(horizontal = 2.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    PostView()
}

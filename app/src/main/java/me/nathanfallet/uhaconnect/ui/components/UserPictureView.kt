package me.nathanfallet.uhaconnect.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.extensions.pictureUrl
import me.nathanfallet.uhaconnect.models.User

@Composable
fun UserPictureView(
    modifier: Modifier = Modifier,
    user: User?,
    size: Dp
) {
    user?.pictureUrl?.let {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(it)
                .memoryCacheKey(it)
                .networkCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = user.username,
            placeholder = painterResource(id = R.drawable.picture_placeholder),
            error = painterResource(id = R.drawable.picture_placeholder),
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
        )
    } ?: Image(
        painter = painterResource(id = R.drawable.picture_placeholder),
        contentDescription = user?.username,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
    )
}

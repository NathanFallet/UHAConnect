import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.Post

class FavsViewModel : ViewModel() {
    fun getFavoritePosts(): List<Post> {
        val instant: kotlinx.datetime.Instant = kotlinx.datetime.Instant.parse("2023-06-07T12:34:56Z")

        return listOf(
            Post(
                id = 1,
                title = "Favorite Post 1",
                content = "This is my favorite post",
                user_id = 1,
                date = instant
            ),
            Post(
                id = 2,
                title = "Favorite Post 2",
                content = "This is another favorite post",
                user_id = 2,
                date = instant
            ),
        )
    }


}

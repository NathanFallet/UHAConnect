package me.nathanfallet.uhaconnect.features.Favs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.Comment
import me.nathanfallet.uhaconnect.models.Post
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.services.APIService

class FavsViewModel(token : String) : ViewModel() {

    private val _favPosts = MutableLiveData<List<Post>>()
    val favPosts: LiveData<List<Post>>
        get() = _favPosts

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user
    init {

        viewModelScope.launch {
            try {
                val instant: kotlinx.datetime.Instant = kotlinx.datetime.Instant.parse("2023-06-07T12:34:56Z")
                val apiService = APIService.getInstance(Unit)
                val user = apiService.getUser(token, 78)
                val favPosts = listOf(
                    Post(11,11,"TITLE","THIS IS THE CONTENT",instant),
                    Post(11,11,"TITLE","THIS IS THE CONTENT",instant),
                    Post(11,11,"TITLE","THIS IS THE CONTENT",instant),
                    Post(11,11,"TITLE","THIS IS THE CONTENT",instant),

                )

                _favPosts.value = favPosts
                _user.value = user
            } catch (e: Exception) {
                null
            }
        }

    }

    /*fun getFavoritePosts(): List<Post> {
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
    }*/


}

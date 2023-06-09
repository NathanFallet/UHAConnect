package me.nathanfallet.uhaconnect.features.feed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.Post
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.services.APIService

class FeedViewModel(
    application: Application
) : AndroidViewModel(application) {


    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>>
        get() = _posts

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun loadData(token: String?) {
        if (token == null) {
            return
        }
        viewModelScope.launch {
            try {
                _posts.value = APIService.getInstance(Unit).getPosts(token)
            }
            catch (e: Exception){}
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



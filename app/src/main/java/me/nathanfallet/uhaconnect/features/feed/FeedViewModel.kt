package me.nathanfallet.uhaconnect.features.feed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonNull.content
import me.nathanfallet.uhaconnect.models.CreateCommentPayload
import me.nathanfallet.uhaconnect.models.Favorite
import me.nathanfallet.uhaconnect.models.Post
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.services.APIService

class FeedViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val isFavorite: Boolean? = savedStateHandle["isFavorite"]

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>>
        get() = _posts

    private val _user = MutableLiveData<User>()

    private val api = APIService.getInstance(Unit)

    fun loadData(token: String?) {
        if (token == null) {
            return
        }
        viewModelScope.launch {
            try {
                _posts.value = if (isFavorite == true) api.getFavorites(token)
                else api.getPosts(token)
            }
            catch (e: Exception){}
        }
    }
    fun deletePost(token: String?, idPost: Int) {
        viewModelScope.launch {
            try {
                val api = APIService.getInstance(Unit)
                api.deletePost(token!!, idPost)

            } catch (e: Exception) {
                //TODO: ERRORS
            }
        }
    }

    fun favoritesHandle(token: String?, postId: Int, addOrDelete: Boolean){
        if (token == null) return
        viewModelScope.launch {
            try {
                if (!addOrDelete) api.addToFavorites(token, postId)
                else api.deleteToFavorites(token, postId)
                loadData(token)
            } catch (e: Exception) {
            }
        }
    }

}




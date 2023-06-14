package me.nathanfallet.uhaconnect.features.feed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.Favorite
import me.nathanfallet.uhaconnect.models.Post
import me.nathanfallet.uhaconnect.models.RoleStatus
import me.nathanfallet.uhaconnect.models.UpdatePostPayload
import me.nathanfallet.uhaconnect.models.UpdateUserPayload
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.services.APIService

class FeedViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    val loader: String? = savedStateHandle["loader"]

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>>
        get() = _posts

    private val _hasMore = MutableLiveData(true)
    val hasMore: LiveData<Boolean>
        get() = _hasMore

    fun loadData(token: String?, reset: Boolean) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val offset = (if (reset) 0 else _posts.value?.size ?: 0).toLong()
                when (loader) {
                    "posts" -> APIService.getInstance(Unit).getPosts(token, offset)
                    "favorites" -> APIService.getInstance(Unit).getFavorites(token, offset)
                    "validation" -> APIService.getInstance(Unit).getPostsRequests(token, offset)
                    "following" -> APIService.getInstance(Unit).getPostsFollowing(token, offset)
                    else -> listOf()
                }.let {
                    _posts.value =
                        if (reset) it
                        else (_posts.value ?: listOf()) + it
                    _hasMore.value = it.isNotEmpty()
                }
            } catch (e: Exception) {

            }
        }
    }

    fun deletePost(token: String?, id: Int) {
        if (token == null) return
        viewModelScope.launch {
            try {
                APIService.getInstance(Unit).deletePost(token, id)
                _posts.value = _posts.value?.filter { it.id != id }
            } catch (e: Exception) {
                //TODO: ERRORS
            }
        }
    }

    fun updatePost(token: String?, id: Int, payload: UpdatePostPayload) {
        if (token == null) return
        viewModelScope.launch {
            try {
                APIService.getInstance(Unit).updatePost(token, id, payload)
                if (payload == UpdatePostPayload(validated = true)) {
                    _posts.value = _posts.value?.filter { it.id != id }
                }
            } catch (e: Exception) {
                //TODO: ERRORS
            }
        }
    }
    fun updateUser(token: String?, id:Int) {
        if (token == null) return
        viewModelScope.launch {
            try {
                    APIService.getInstance(Unit).updateUser(
                        token,
                        id,
                        UpdateUserPayload(role = RoleStatus.BANNED)
                    )
            } catch (e: Exception) {
                //TODO : ERRORS
            }
        }
    }

    fun favoritesHandle(token: String?, postId: Int, addOrDelete: Boolean) {
        if (token == null) return
        viewModelScope.launch {
            try {
                val favorite: Favorite? =
                    if (!addOrDelete) APIService.getInstance(Unit).addToFavorites(token, postId)
                    else {
                        APIService.getInstance(Unit).deleteToFavorites(token, postId)
                        null
                    }
                _posts.value = _posts.value?.map {
                    if (it.id == postId) {
                        it.copy(favorite = favorite)
                    } else it
                }
            } catch (e: Exception) {
            }
        }
    }

}




package me.nathanfallet.uhaconnect.features.post

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.Comment
import me.nathanfallet.uhaconnect.models.CreateCommentPayload
import me.nathanfallet.uhaconnect.models.Post
import me.nathanfallet.uhaconnect.models.UpdatePostPayload
import me.nathanfallet.uhaconnect.services.APIService

class PostViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val postId: Int? = savedStateHandle["postId"]

    val newComment = MutableLiveData("")

    private val _post = MutableLiveData<Post>()
    val post: LiveData<Post>
        get() = _post

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>>
        get() = _comments

    fun loadPost(token: String?) {
        if (postId == null || token == null) return
        viewModelScope.launch {
            try {
                _post.value = APIService.getInstance(Unit).getPost(token, postId)
            } catch (e: Exception) {

            }
        }
    }

    fun loadComments(token: String?) {
        if (postId == null || token == null) return
        viewModelScope.launch {
            try {
                _comments.value = APIService.getInstance(Unit).getComments(token, postId)
            } catch (e: Exception) {

            }
        }
    }

    fun sendComment(token: String?) {
        if (newComment.value.isNullOrBlank()) {
            // TODO: Show error
            return
        }
        if (token == null || postId == null) return
        viewModelScope.launch {
            _comments.value = _comments.value?.plus(
                APIService.getInstance(Unit).postComment(
                    token,
                    postId,
                    CreateCommentPayload(newComment.value ?: "")
                )
            )
        }
    }

    fun deleteComment(token: String?, idPost: Int, idComment: Int) {
        if (token == null) return
        viewModelScope.launch {
            try {
                APIService.getInstance(Unit).deleteComment(token, idPost, idComment)
            } catch (e: Exception) {
                //TODO: ERRORS
            }
        }
    }
    fun deletePost(token: String?, idPost: Int) {
        if (token == null) return
        viewModelScope.launch {
            try {
                APIService.getInstance(Unit).deletePost(token, idPost)
            } catch (e: Exception) {
                //TODO: ERRORS
            }
        }
    }

    fun favoritesHandle(token: String?, postId: Int, addOrDelete: Boolean){
        if (token == null) return
        viewModelScope.launch {
            try {
                if (addOrDelete) APIService.getInstance(Unit).addToFavorites(token, postId)
                else APIService.getInstance(Unit).deleteToFavorites(token, postId)
                loadPost(token)
            } catch (e: Exception) {
            }
        }
    }

    fun updatePost(token: String?, id: Int, payload: UpdatePostPayload) {
        if (token == null) return
        viewModelScope.launch {
            try {
                APIService.getInstance(Unit).updatePost(token, id, payload)
            } catch (e: Exception) {
                //TODO: ERRORS
            }
        }
    }

}

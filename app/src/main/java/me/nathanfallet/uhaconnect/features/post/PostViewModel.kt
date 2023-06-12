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
import me.nathanfallet.uhaconnect.services.APIService

class PostViewModel(application: Application,
                    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    val newComment = MutableLiveData("")

    private val postId: Int? = savedStateHandle["postId"]

    private val _post = MutableLiveData<Post>()
    val post: LiveData<Post>
        get() = _post

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>>
        get() = _comments

    fun loadData(token: String?){
        if (postId == null || token == null)
            return
        viewModelScope.launch {
            try {
                val api = APIService.getInstance(Unit)
                _post.value = api.getPost(token, postId)
                _comments.value = api.getComments(token, postId)
            } catch (e: Exception) {
                null
            }
        }
    }
    fun sendComment(token: String?) {

        val content = newComment.value
        val api = APIService.getInstance(Unit)

        if (token == null || postId == null || content.isNullOrBlank()) {
            return
        }
        viewModelScope.launch {
            _comments.value = _comments.value?.plus(
                api.postComment(
                    token,
                    postId,
                    CreateCommentPayload(content)
                )
            )
        }
    }

    suspend fun deleteComment(token: String?, idPost: Int, idComment: Int) {
        try {
            val api = APIService.getInstance(Unit)

            val updatedComments = api.deleteComment(token, idPost, idComment)
            _comments.value = updatedComments
        } catch (e: Exception) {
            //TODO: ERRORS
        }

    }
    suspend fun deletePost(token: String?, idPost: Int) {
        try {
            val api = APIService.getInstance(Unit)
            api.deletePost(token!!, idPost)

        } catch (e: Exception) {
            //TODO: ERRORS
        }

    }

}

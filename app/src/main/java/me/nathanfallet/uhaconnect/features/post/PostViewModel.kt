package me.nathanfallet.uhaconnect.features.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.Comment
import me.nathanfallet.uhaconnect.models.Post
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.services.APIService

class PostViewModel(token : String,
                    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val postId: Int? = savedStateHandle["postId"]
    private val userId: Int? = savedStateHandle["userId"]

    private val _post = MutableLiveData<Post>()
    val post: LiveData<Post>
        get() = _post

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user


    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>>
        get() = _comments


    fun loadData(token: String?){
        if (postId == null || userId == null || token == null)
            return
        viewModelScope.launch {
            try {
                val api = APIService.getInstance(Unit)
                _user.value = api.getUser(token, userId)
                _post.value = api.getPost(token, postId)
                _comments.value = api.getComments(token, postId)
            } catch (e: Exception) {
                null
            }
        }

    }
}

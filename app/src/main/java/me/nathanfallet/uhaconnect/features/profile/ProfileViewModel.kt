package me.nathanfallet.uhaconnect.features.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.Api
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.R
import me.nathanfallet.uhaconnect.models.LoginPayload
import me.nathanfallet.uhaconnect.models.Post
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.models.UserToken
import me.nathanfallet.uhaconnect.services.APIService

class ProfileViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val id: Int? = savedStateHandle["userId"]

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>>
        get() = _posts

    private val api = APIService.getInstance(Unit)

    fun loadData(token: String?) {
        if (token == null || id == null) {
            return
        }
        viewModelScope.launch {
            try {
                _user.value = api.getUser(id, token)
                _posts.value = api.getUserPosts(id, token)
            }
            catch (e: Exception){}
        }
    }

    fun favoritesHandle(token: String?, postId: Int, addOrDelete: Boolean){
        if (token == null) return
        viewModelScope.launch {
            try {
                if (addOrDelete) api.addToFavorites(token, postId)
                else api.deleteToFavorites(token, postId)
                loadData(token)
            } catch (e: Exception) {
            }
        }
    }
}
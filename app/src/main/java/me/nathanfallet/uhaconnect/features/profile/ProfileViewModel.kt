package me.nathanfallet.uhaconnect.features.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.Post
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.services.APIService

class ProfileViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>>
        get() = _posts

    fun loadData(token: String?, id: Int?) {
        if (token == null || id == null) {
            return
        }
        viewModelScope.launch {
            try {
                _posts.value = APIService.getInstance(Unit).getUserPosts(id, token)
            }
            catch (e: Exception){}
        }
    }




}
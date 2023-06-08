package me.nathanfallet.uhaconnect.features.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.Post
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.services.APIService

class ProfileViewModel(
    application: Application,
    private val UserId:Int,
) : AndroidViewModel(application) {


    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>>
        get() = _posts

    fun loadData(token: String) {
        viewModelScope.launch {
            try {
                APIService.getInstance(Unit).getUser(UserId, token)
                APIService.getInstance(Unit).getUserPosts(UserId, token)
            }
            catch (e: Exception){}
        }
    }




}
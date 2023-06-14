package me.nathanfallet.uhaconnect.features.follows

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.Follow
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.services.APIService

class FollowsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val id: Int? = savedStateHandle["userId"]

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _follows = MutableLiveData<List<User>>()
    val follows: LiveData<List<User>>
        get() = _follows

    private val _hasMore = MutableLiveData(true)
    val hasMore: LiveData<Boolean>
        get() = _hasMore

    fun loadUser(token: String?) {
        if (token == null || id == null) return
        viewModelScope.launch {
            try {
                _user.value = APIService.getInstance(Unit).getUser(id, token)
            } catch (e: Exception) {

            }
        }
    }

    fun loadFollows(token: String?, reset: Boolean) {
        if (token == null || id == null) return
        viewModelScope.launch {
            try {
                val offset = (if (reset) 0 else _follows.value?.size ?: 0).toLong()
                APIService.getInstance(Unit).getFollows(token, id, offset).let {
                    _follows.value =
                        if (reset) it
                        else (_follows.value ?: listOf()) + it
                    _hasMore.value = it.isNotEmpty()
                }
            } catch (e: Exception) {

            }
        }
    }

    fun followHandle(token: String?, id: Int?, isFollowed: Boolean){
        if (token == null || id == null) return
        viewModelScope.launch{
            try {
                val follow: Follow? =
                    if (isFollowed) {
                        APIService.getInstance(Unit).unfollow(token, id)
                        null
                    }
                    else APIService.getInstance(Unit).follow(token, id)
                _follows.value = _follows.value?.map {
                    if (it.id == id) it.copy(follow = follow)
                    else it
                }
            }
            catch (e: Exception){}
        }
    }

}
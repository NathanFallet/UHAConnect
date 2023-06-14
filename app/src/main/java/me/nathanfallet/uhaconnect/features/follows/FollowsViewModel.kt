package me.nathanfallet.uhaconnect.features.follows

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.User
import me.nathanfallet.uhaconnect.services.APIService

class FollowsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val id: Int? = savedStateHandle["userId"]
    val loader: String? = savedStateHandle["loader"]

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
                (if (loader=="following") APIService.getInstance(Unit).getFollowing(token, id, offset)
                        else APIService.getInstance(Unit).getFollowers(token, id, offset)).let {
                    _follows.value =
                        if (reset) it
                        else (_follows.value ?: listOf()) + it
                    _hasMore.value = it.isNotEmpty()
                }
            } catch (e: Exception) {

            }
        }
    }


}
package me.nathanfallet.uhaconnect.features.compose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.CreatePostPayload
import me.nathanfallet.uhaconnect.services.APIService

class ComposeViewModel(token : String?): ViewModel() {

    val postContent = MutableLiveData("")
    val titleContent = MutableLiveData("")

    private val _id = MutableLiveData<Int>()
    val id: LiveData<Int>
        get() = _id

    fun post(token: String?) {
        val title = titleContent.value
        val content = postContent.value

        if (token == null || title.isNullOrBlank() || content.isNullOrBlank()) {
            return
        }
        viewModelScope.launch {
            _id.value = APIService.getInstance(Unit).postPost(token, CreatePostPayload(
                title, content ?: ""
            )).id
        }
    }

}
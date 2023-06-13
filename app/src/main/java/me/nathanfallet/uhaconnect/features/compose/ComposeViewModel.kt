package me.nathanfallet.uhaconnect.features.compose

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.CreatePostPayload
import me.nathanfallet.uhaconnect.services.APIService


class ComposeViewModel : ViewModel() {

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
            _id.value = APIService.getInstance(Unit).postPost(
                token, CreatePostPayload(title, content)
            ).id
        }
    }

    fun selectMedia(token: String?, uri: Uri?, context: Context) {
        if (token == null || uri == null) return
        viewModelScope.launch {
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use {
                    it.readBytes()
                } ?: ByteArray(0)

                val isVideo = context
                    .contentResolver
                    .getType(uri)
                    ?.startsWith("video/") ?: false

                val payload = APIService.getInstance(Unit).uploadMedia(token, bytes, isVideo)
                val imageUrl = "${APIService.baseUrl}/media/${payload.fileName}"
                postContent.value =  postContent.value + "\n" + "![]($imageUrl)"
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception, e.g., show an error message or retry
            }
        }
    }

}

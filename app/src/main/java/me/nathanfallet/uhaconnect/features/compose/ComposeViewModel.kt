package me.nathanfallet.uhaconnect.features.compose

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.CreatePostPayload
import me.nathanfallet.uhaconnect.models.Post
import me.nathanfallet.uhaconnect.services.APIService


class ComposeViewModel : ViewModel() {

    val postContent = MutableLiveData("")
    val titleContent = MutableLiveData("")

    fun post(token: String?, navigate: (String) -> Unit) {
        if (token == null) return
        if (!Post.isTitleValid(titleContent.value ?: "")) {
            // TODO: Show error
            return
        }
        if (postContent.value.isNullOrBlank()) {
            // TODO: Show error
            return
        }
        viewModelScope.launch {
            try {
                APIService.getInstance(Unit).postPost(
                    token, CreatePostPayload(
                        titleContent.value ?: "",
                        postContent.value ?: "",
                        listOf()
                    )
                ).let {
                    navigate("post/${it.id}")
                }
            } catch (e: Exception) {

            }
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

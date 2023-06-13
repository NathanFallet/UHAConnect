package me.nathanfallet.uhaconnect.features.compose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.CreatePostPayload
import me.nathanfallet.uhaconnect.services.APIService
import dev.jeziellago.compose.markdowntext.MarkdownText


class ComposeViewModel : ViewModel() {

    val postContent = MutableLiveData("")
    val titleContent = MutableLiveData("")

    private val _id = MutableLiveData<Int>()
    val id: LiveData<Int>
        get() = _id

    private val _image = MutableLiveData<Bitmap>()
    val image: LiveData<Bitmap>
        get() = _image

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String>
        get() = _imageUrl

    private val _fileName = MutableLiveData<String>()
    val fileName: LiveData<String>
        get() = _fileName

    fun post(token: String?) {
        val title = titleContent.value
        val content = postContent.value
        val filename = _fileName.value

        if (token == null || title.isNullOrBlank() || content.isNullOrBlank()) {
            return
        }
        viewModelScope.launch {
            val postId = APIService.getInstance(Unit).postPost(
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
                val fileName = payload.fileName
                _fileName.value = fileName
                _imageUrl.value = "${APIService.baseUrl}/media/$fileName"
                _image.value = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                val imageUrl = "https://uhaconnect.nathanfallet.me/media/$fileName"
                postContent.value =  postContent.value + "\n" + "![]($imageUrl)"

            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception, e.g., show an error message or retry
            }
        }
    }

}

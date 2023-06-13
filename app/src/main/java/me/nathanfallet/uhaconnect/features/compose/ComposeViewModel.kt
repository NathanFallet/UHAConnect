package me.nathanfallet.uhaconnect.features.compose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.nathanfallet.uhaconnect.models.CreatePostPayload
import me.nathanfallet.uhaconnect.models.MediaPayload
import me.nathanfallet.uhaconnect.models.Post
import me.nathanfallet.uhaconnect.services.APIService


class ComposeViewModel : ViewModel() {

    val postContent = MutableLiveData("")
    val titleContent = MutableLiveData("")

    private val _id = MutableLiveData<Int>()
    val id: LiveData<Int>
        get() = _id

    private val _image = MutableLiveData<Bitmap>()
    val image: LiveData<Bitmap>
        get() = _image

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

    fun selectMedia(token: String, uri: Uri, context: Context) {
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
                _image.value = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception, e.g., show an error message or retry
            }
        }
    }
}

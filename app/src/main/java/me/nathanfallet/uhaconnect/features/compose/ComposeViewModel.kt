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
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.models.CreatePostPayload
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


    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String>
        get() = _imageUrl


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
                APIService.getInstance(Unit).uploadMedia(token, bytes, isVideo)
                val imageUrl = APIService.getInstance(Unit).uploadMedia(token, bytes, isVideo)


                _imageUrl.value = imageUrl.bodyAsText()

                _image.value = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
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

class ComposeViewModel : ViewModel() {

    val postContent = MutableLiveData("")
    val titleContent = MutableLiveData("")

    private val _id = MutableLiveData<Int>()
    val id: LiveData<Int>
        get() = _id


    private val _image = MutableLiveData<Bitmap>()
    val image: LiveData<Bitmap>
        get() = _image

    fun post(token: String?) {
        val title = titleContent.value
        val content = postContent.value

        if (token == null || title.isNullOrBlank() || content.isNullOrBlank()) {
            return
        }
        viewModelScope.launch {
            _id.value = APIService.getInstance().postPost(
                token, CreatePostPayload(
                    title, content ?: ""
                )
            ).id
        }
    }

    fun selectMedia(token: String, uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val mediaData = inputStream?.use { it.readBytes() } ?: ByteArray(0)
                inputStream?.close()

                if (uri.toString().startsWith("content://")) {
                    context.contentResolver.delete(uri, null, null)
                }

                val isVideo = context.contentResolver.getType(uri)?.startsWith("video/") ?: false

                val apiService = APIService.getInstance()
                if (isVideo) {
                    apiService.selectMedia(token, mediaData, true)
                } else {
                    apiService.selectMedia(token, mediaData, false)
                }

                _image.value = BitmapFactory.decodeByteArray(mediaData, 0, mediaData.size)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}
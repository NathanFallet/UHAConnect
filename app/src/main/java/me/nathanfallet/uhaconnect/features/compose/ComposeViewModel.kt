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
            _id.value = APIService.getInstance(Unit).postPost(
                token, CreatePostPayload(title, content)
            ).id
        }
    }

    fun selectImage(token: String, uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use {
                    it.readBytes()
                } ?: ByteArray(0)
                APIService.getInstance(Unit).selectImage(token, bytes)

                _image.value = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}
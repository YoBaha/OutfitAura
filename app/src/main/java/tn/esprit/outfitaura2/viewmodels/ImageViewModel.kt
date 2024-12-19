package tn.esprit.outfitaura2.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ImageViewModel : ViewModel() {
    private val _imageList = MutableStateFlow<List<Pair<Bitmap, String>>>(emptyList())
    val imageList: StateFlow<List<Pair<Bitmap, String>>> = _imageList

    fun addImage(bitmap: Bitmap, label: String) {
        _imageList.value = _imageList.value + (bitmap to label)
    }
}

package tn.esprit.outfitaura2.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    // Mutable StateFlow to hold the list of images and predictions
    private val _imageList = MutableStateFlow<List<Pair<Bitmap, String>>>(emptyList())

    // Public StateFlow to expose the list to the UI
    val imageList: StateFlow<List<Pair<Bitmap, String>>> = _imageList

    // Method to add an image and its prediction
    fun addImage(bitmap: Bitmap, prediction: String) {
        // Update the StateFlow with the new image and prediction
        val updatedList = _imageList.value.toMutableList().apply {
            add(bitmap to prediction)
        }
        _imageList.value = updatedList
    }
}

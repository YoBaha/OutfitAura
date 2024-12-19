package tn.esprit.outfitaura2.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {

    // Using MutableStateFlow for the image list to expose it as a StateFlow
    private val _imageList = MutableStateFlow<List<Pair<Bitmap, String>>>(emptyList())
    val imageList: StateFlow<List<Pair<Bitmap, String>>> = _imageList

    // MutableStateFlow for the selected style
    private val _selectedStyle = MutableStateFlow<String>("casual wear")
    val selectedStyle: StateFlow<String> = _selectedStyle

    // Method to add an image and its label/prediction
    fun addImage(image: Bitmap, label: String) {
        // Update the image list by adding the new image with its label
        _imageList.value = _imageList.value + Pair(image, label)
    }

    // Method to get images filtered by style (the label)
    fun getImagesByStyle(style: String): List<Pair<Bitmap, String>> {
        // Filter the image list by the style/label, ignoring case
        return _imageList.value.filter { it.second.contains(style, ignoreCase = true) }
    }

    // Method to update the selected style
    fun updateStyle(style: String) {
        _selectedStyle.value = style
    }

    // New method to update the selected style from an external activity
    fun setStyleFromActivity(style: String) {
        _selectedStyle.value = style
    }
}

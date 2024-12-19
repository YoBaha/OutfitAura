package tn.esprit.outfitaura2.viewmodels

import tn.esprit.outfitaura2.ui.theme.StyleButton
import tn.esprit.outfitaura2.ui.theme.ImageGrid
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import tn.esprit.outfitaura2.R
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import tn.esprit.outfitaura2.ui.theme.OutfitAura2Theme
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.util.Base64

class WardrobeActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val imageList = mutableListOf<Pair<Bitmap, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the base64 image string from the Intent
        val base64Image = intent.getStringExtra("image")
        base64Image?.let {
            val bitmap = convertBase64ToBitmap(it)
            imageList.add(Pair(bitmap, "Uploaded Image"))
            // Add the image to the ViewModel's list of images
            homeViewModel.addImage(bitmap, "Uploaded Image")
        }

        setContent {
            // Observe the selected style from the HomeViewModel
            val selectedStyle by homeViewModel.selectedStyle.collectAsState()

            OutfitAura2Theme {
                Scaffold(
                    bottomBar = {
                        BottomAppBar {
                            // Add navigation buttons if needed
                        }
                    },
                    content = { paddingValues ->
                        Column(
                            modifier = Modifier
                                .padding(paddingValues)
                        ) {
                            // Style buttons to allow style selection
                            StyleButton("Athleisure", "athleisure") { homeViewModel.updateStyle(it) }
                            StyleButton("Casual Wear", "casual wear") { homeViewModel.updateStyle(it) }
                            StyleButton("Formal Wear", "formal wear") { homeViewModel.updateStyle(it) }
                            StyleButton("Winter Wear", "winter wear") { homeViewModel.updateStyle(it) }

                            // Filter images by selected style using the getImagesByStyle method
                            val filteredImages = homeViewModel.getImagesByStyle(selectedStyle)

                            // Display the filtered images using WardrobeScreen
                            WardrobeScreen(filteredImages)
                        }
                    }
                )
            }
        }
    }

    // Function to convert Base64 string to Bitmap
    fun convertBase64ToBitmap(base64String: String): Bitmap {
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}

@Composable
fun WardrobeScreen(imageList: List<Pair<Bitmap, String>>) {
    var selectedStyle by remember { mutableStateOf("") } // Track selected style for filtering

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Add your StyleButton composables here (similar to HomeActivity)
            item {
                StyleButton("Athleisure", "athleisure") { selectedStyle = it }
            }
            item {
                StyleButton("Casual Wear", "casual wear") { selectedStyle = it }
            }
            item {
                StyleButton("Formal Wear", "formal wear") { selectedStyle = it }
            }
            item {
                StyleButton("Winter Wear", "winter wear") { selectedStyle = it }
            }

            // Filter and display images based on selected style
            val filteredImages = imageList.filter { pair ->
                selectedStyle.isEmpty() || pair.second.contains(selectedStyle, ignoreCase = true)
            }
            item {
                ImageGrid(filteredImages)
            }

            // Add spacing at the bottom to avoid overlapping with BottomAppBar
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        // Bottom Navigation Bar (same as in HomeActivity)
    }
}

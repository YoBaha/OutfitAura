package tn.esprit.outfitaura2.view

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import tn.esprit.outfitaura2.viewmodels.HomeViewModel

class StyleRecommendationsActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the selected style from the Intent
        val selectedStyle = intent.getStringExtra("SELECTED_STYLE") ?: "casual wear"
        Log.d("StyleRecommendations", "Selected style: $selectedStyle")  // Debugging log

        // Update the ViewModel with the selected style
        homeViewModel.setStyleFromActivity(selectedStyle)

        // Set the content with the selected style
        setContent {
            StyleRecommendationScreen(
                selectedStyle = selectedStyle,
                homeViewModel = homeViewModel
            )
        }
    }
}


@Composable
fun StyleRecommendationScreen(
    selectedStyle: String,
    homeViewModel: HomeViewModel
) {
    // Call getImagesByStyle to get the filtered images based on the selected style
    val filteredImages = homeViewModel.getImagesByStyle(selectedStyle)

    Log.d("StyleRecommendation", "Filtered imageList size: ${filteredImages.size}")  // Debugging log

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Recommended Clothes",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredImages) { pair ->
                val (image, prediction) = pair
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        bitmap = image.asImageBitmap(),
                        contentDescription = prediction,
                        modifier = Modifier.size(150.dp),
                    )
                    Text(
                        text = prediction,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

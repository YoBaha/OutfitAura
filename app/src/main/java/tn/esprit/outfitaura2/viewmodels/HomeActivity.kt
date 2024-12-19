package tn.esprit.outfitaura2.viewmodels

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import tn.esprit.outfitaura2.R
import tn.esprit.outfitaura2.ui.theme.OutfitAura2Theme
import java.io.File
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import tn.esprit.outfitaura2.LoginActivity
import tn.esprit.outfitaura2.view.SessionManager
import android.util.Base64
import tn.esprit.outfitaura2.view.StyleRecommendationsActivity
import java.io.ByteArrayOutputStream


class HomeActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private lateinit var photoUri: Uri
    private lateinit var clothingClassifier: ClothingClassifier
    private lateinit var styleClassifier: StyleClassifier

    private val clothingLabels = listOf(
        "dress", "hat", "hoodie", "longsleeve", "outerwear",
        "pants", "shirt", "shoes", "shorts", "skirt", "t-shirt"
    )

    private val styleLabels = listOf(
        "athleisure", "casual wear", "formal wear", "winter wear"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize classifiers
        clothingClassifier = ClothingClassifier("clothing_model.tflite", assets)
        styleClassifier = StyleClassifier("clothing_style.tflite", assets)

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) processCapturedPhoto()
            else showToast("Photo capture failed.")
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { processSelectedPhoto(it) }
        }

        setContent {
            OutfitAura2Theme {
                val imageList by homeViewModel.imageList.collectAsState()

                HomeScreen(
                    onCameraClick = { checkCameraPermission() },
                    onGalleryClick = { openGallery() },
                    imageList = imageList,
                    onAddImage = { bitmap, label ->
                        homeViewModel.addImage(bitmap, label)
                    }
                )
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun openCamera() {
        val tempFile = File.createTempFile("photo_", ".jpg", cacheDir).apply {
            deleteOnExit()
        }
        photoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", tempFile)
        cameraLauncher.launch(photoUri)
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun processCapturedPhoto() {
        contentResolver.openInputStream(photoUri)?.use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
            classifyAndStore(bitmap)
        }
    }

    private fun processSelectedPhoto(uri: Uri) {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
            classifyAndStore(bitmap)
        }
    }

    private fun classifyAndStore(bitmap: Bitmap) {
        // Predict clothing item
        val clothingIndex = clothingClassifier.classify(bitmap)
        val clothingLabel = clothingLabels.getOrElse(clothingIndex) { "Unknown" }

        // Predict clothing style
        val styleIndex = styleClassifier.classify(bitmap)
        val styleLabel = styleLabels.getOrElse(styleIndex) { "Unknown" }

        showToast("Clothing: $clothingLabel, Style: $styleLabel")
        homeViewModel.addImage(bitmap, "$clothingLabel, $styleLabel")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val CAMERA_PERMISSION_CODE = 1001
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    imageList: List<Pair<Bitmap, String>>,
    onAddImage: (Bitmap, String) -> Unit
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color.White, Color(0xFFDCDCDC)) // White to grey gradient
    )

    var searchText by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("") } // Track selected style

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Box
            item {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Search...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Gray,
                        unfocusedIndicatorColor = Color.LightGray,
                        cursorColor = Color.Gray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // Add Buttons for Style Selection
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

            // Marketing Cards (Now Just Images with Text)
            item {
                MarketingCard(
                    imageResId = R.drawable.card,
                    text = "Discover our latest features and tools!"
                )
            }

            item {
                MarketingCard(
                    imageResId = R.drawable.card2,
                    text = "Explore new possibilities with our services!"
                )
            }

            // Image Grid Section
            val filteredImages = imageList.filter { pair ->
                selectedStyle.isEmpty() || pair.second.contains(selectedStyle, ignoreCase = true)
            }

            if (filteredImages.isNotEmpty()) {
                item {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 150.dp), // Make grid adaptive
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .heightIn(max = 400.dp)
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
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(280.dp)
                                        .padding(4.dp),
                                    contentScale = ContentScale.Crop
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

            // Add spacing at the bottom to avoid overlapping with BottomAppBar
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        // Bottom Navigation Bar
        BottomAppBar(
            containerColor = Color.Black,
            contentColor = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WardrobeButton()

                // Profile Button (Placeholder)
                IconButton(
                    onClick = { /* Handle Profile Button Click */ },
                    modifier = Modifier
                        .size(60.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile), // Replace with appropriate drawable resource
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Upload Button
                IconButton(
                    onClick = onGalleryClick,
                    modifier = Modifier
                        .size(60.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.upload), // Replace with appropriate drawable resource
                        contentDescription = "Upload Photo",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Camera Button
                IconButton(
                    onClick = onCameraClick,
                    modifier = Modifier
                        .size(60.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.camera), // Replace with appropriate drawable resource
                        contentDescription = "Capture Photo",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun StyleButton(styleName: String, styleTag: String, onStyleSelected: (String) -> Unit) {
    Button(
        onClick = { onStyleSelected(styleTag) },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBCFF5E))
    ) {
        Text(text = styleName, color = Color.Black)
    }
}


@Composable
fun MarketingCard(imageResId: Int, text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFF282B30), shape = RoundedCornerShape(8.dp)), // Set dark background color and rounded corners
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth() // Image takes full width of the card
                .height(200.dp) // Fixed height for consistency
                .clip(RoundedCornerShape(8.dp)), // Apply rounded corners to the image
            contentScale = ContentScale.Crop // Crop the image to fit
        )
        Spacer(modifier = Modifier.height(8.dp)) // Add space between image and text
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp), // Ensure text is readable
            color = Color.White, // White text for contrast
            modifier = Modifier.padding(horizontal = 8.dp) // Add horizontal padding
        )
        Spacer(modifier = Modifier.height(8.dp)) // Add space between text and button
        Button(
            onClick = {}, // No action for now
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth() // Button spans full width
                .height(48.dp), // Set height for button
            shape = RoundedCornerShape(8.dp), // Rounded button corners
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBCFF5E)) // Set button color to #BCFF5E
        ) {
            Text(text = "Learn More", color = Color.Black) // Button text in white for contrast
        }
    }
}

// Move WardrobeButton outside of HomeScreen for organization
@Composable
fun WardrobeButton() {
    val context = LocalContext.current // Get the context in a Composable

    IconButton(
        onClick = {
            val intent = Intent(context, WardrobeActivity::class.java)
            context.startActivity(intent)
        },
        modifier = Modifier
            .size(60.dp)
            .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
    ) {
        Image(
            painter = painterResource(id = R.drawable.wardrobe), // Replace with appropriate drawable resource
            contentDescription = "Wardrobe",
            modifier = Modifier.fillMaxSize()
        )
    }
}
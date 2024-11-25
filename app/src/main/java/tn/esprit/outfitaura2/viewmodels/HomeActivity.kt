package tn.esprit.outfitaura2.viewmodels

import android.Manifest
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import tn.esprit.outfitaura2.R
import tn.esprit.outfitaura2.ui.theme.OutfitAura2Theme
import java.io.File

class HomeActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels() // ViewModel instance

    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private lateinit var photoUri: Uri
    private lateinit var classifier: ClothingClassifier

    private val labels = listOf(
        "dress", "hat", "hoodie", "longsleeve", "outwear",
        "pants", "shirt", "shoes", "shorts", "skirt", "t-shirt"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        classifier = ClothingClassifier("clothing_model.tflite", assets)

        // Register camera and gallery launchers
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                processCapturedPhoto()
            } else {
                showToast("Failed to capture photo.")
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                processSelectedPhoto(it)
            }
        }

        setContent {
            OutfitAura2Theme {
                val imageList by homeViewModel.imageList.collectAsState()

                HomeScreen(
                    onCameraClick = { checkPermissions() },
                    onGalleryClick = { openGallery() },
                    imageList = imageList,
                    onAddImage = { bitmap, label ->
                        homeViewModel.addImage(bitmap, label) // Update ViewModel state
                    }
                )
            }
        }
    }

    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_REQUEST_CODE
                )
            }
            else -> {
                capturePhoto()
            }
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun capturePhoto() {
        try {
            val photoFile = File.createTempFile("photo_", ".jpg", cacheDir)
            photoUri = FileProvider.getUriForFile(
                this,
                "$packageName.fileprovider",
                photoFile
            )
            cameraLauncher.launch(photoUri)
        } catch (e: Exception) {
            showToast("Error creating photo file: ${e.message}")
        }
    }

    private fun processCapturedPhoto() {
        try {
            val inputStream = contentResolver.openInputStream(photoUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            classifyAndStore(bitmap)
        } catch (e: Exception) {
            showToast("Error processing photo: ${e.message}")
        }
    }

    private fun processSelectedPhoto(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            classifyAndStore(bitmap)
        } catch (e: Exception) {
            showToast("Error processing photo: ${e.message}")
        }
    }

    private fun classifyAndStore(bitmap: Bitmap) {
        val predictedIndex = classifier.classify(bitmap)
        val predictedLabel = labels[predictedIndex]

        // Notify the user of the classification
        showToast("Predicted: $predictedLabel")

        // Use the ViewModel to store the image and prediction
        homeViewModel.addImage(bitmap, predictedLabel)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val CAMERA_REQUEST_CODE = 1001
    }
}

@Composable
fun HomeScreen(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    imageList: List<Pair<Bitmap, String>>,
    onAddImage: (Bitmap, String) -> Unit
) {
    val backgroundImage = painterResource(id = R.drawable.bg2)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Background Image
        Image(
            painter = backgroundImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your Wardrobe",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(onClick = onCameraClick) {
                Text("Open Camera")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onGalleryClick) {
                Text("Upload Photo")
            }
            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 16.dp)
            ) {
                items(imageList) { (image, prediction) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            bitmap = image.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .padding(8.dp),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = "Prediction: $prediction",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

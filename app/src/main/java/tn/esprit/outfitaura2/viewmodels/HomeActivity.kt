package tn.esprit.outfitaura2.viewmodels

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.outfitaura2.LoginActivity
import tn.esprit.outfitaura2.viewmodels.MarketplaceActivity
import tn.esprit.outfitaura2.models.DeleteResponse
import tn.esprit.outfitaura2.models.UserImagesResponse
import tn.esprit.outfitaura2.models.UploadResponse
import tn.esprit.outfitaura2.network.ApiClient
import tn.esprit.outfitaura2.network.OnUnauthorizedCallback
import tn.esprit.outfitaura2.ui.theme.OutfitAura2Theme
import tn.esprit.outfitaura2.viewmodels.ClothingClassifier
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : ComponentActivity() {
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val photoUri = currentPhotoUri
            photoUri?.let { uri ->
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                saveImage(bitmap, "camera_${System.currentTimeMillis()}.jpg")
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { saveImageUri(it, "gallery_${System.currentTimeMillis()}.jpg") }
    }

    private var clothingImages by mutableStateOf<List<Pair<String, String>>>(emptyList())
    private var currentPhotoUri by mutableStateOf<Uri?>(null)
    private lateinit var classifier: ClothingClassifier

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            openCameraWithPermission()
        } else {
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        classifier = ClothingClassifier("model_unquant.tflite", assets)
        fetchUserImages()
        setContent {
            OutfitAura2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding),
                        onWardrobeClick = { navigateToWardrobe() },
                        onCameraClick = { requestCameraPermission() },
                        onGalleryClick = { openGallery() },
                        clothingImages = clothingImages,
                        onRecommendationClick = { type, gender -> navigateToRecommendation(type, gender) },
                        onDeleteImage = { imageId ->
                            clothingImages = clothingImages.filter { it.first != imageId }
                        },
                        onMarketplaceClick = { startActivity(Intent(this, MarketplaceActivity::class.java)) }
                    )
                }
            }
        }
    }

    private fun fetchUserImages() {
        val token = getSharedPreferences("auth_prefs", MODE_PRIVATE)
            .getString("jwt_token", null)
        Log.d("HomeActivity", "Fetching images with token: ${token?.take(10) ?: "null"}...")
        val call = ApiClient.getImageService(this).getUserImages()
        ApiClient.tagCall(call, this, OnUnauthorizedCallback { ctx: Context ->
            ctx.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().clear().apply()
            ctx.startActivity(Intent(ctx, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            (ctx as? ComponentActivity)?.finish()
        }).enqueue(object : Callback<UserImagesResponse> {
            override fun onResponse(call: Call<UserImagesResponse>, response: Response<UserImagesResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { userImages ->
                        if (userImages.success) {
                            clothingImages = userImages.images.map { Pair(it.id, it.prediction) }
                            Log.d("HomeActivity", "Fetched images: $clothingImages")
                        } else {
                            Log.e("HomeActivity", "Failed to fetch images: ${userImages.error}")
                            Toast.makeText(this@HomeActivity, "Failed to fetch images", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("HomeActivity", "Fetch images failed: ${response.errorBody()?.string()}")
                    Toast.makeText(this@HomeActivity, "Failed to fetch images", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserImagesResponse>, t: Throwable) {
                Log.e("HomeActivity", "Fetch images failed: ${t.message}")
                Toast.makeText(this@HomeActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToWardrobe() {
        Log.d("HomeActivity", "Navigating to WardrobeActivity with clothingImages: $clothingImages")
        val intent = Intent(this, WardrobeActivity::class.java).apply {
            putStringArrayListExtra("clothingImageIds", ArrayList(clothingImages.map { it.first }))
            putStringArrayListExtra("clothingPredictions", ArrayList(clothingImages.map { it.second }))
        }
        startActivity(intent)
    }

    private fun navigateToRecommendation(type: String, gender: String) {
        Log.d("HomeActivity", "Navigating to RecommendationActivity with type: $type, gender: $gender")
        val intent = Intent(this, RecommendationActivity::class.java).apply {
            putStringArrayListExtra("clothingImageIds", ArrayList(clothingImages.map { it.first }))
            putStringArrayListExtra("clothingPredictions", ArrayList(clothingImages.map { it.second }))
            putExtra("recommendationType", type)
            putExtra("gender", gender)
        }
        startActivity(intent)
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                openCameraWithPermission()
            }
            else -> {
                permissionLauncher.launch(arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ))
            }
        }
    }

    private fun openCameraWithPermission() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photoFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        currentPhotoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
        }
        if (cameraIntent.resolveActivity(packageManager) != null) {
            cameraLauncher.launch(cameraIntent)
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun saveImage(bitmap: Bitmap, fileName: String) {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(storageDir, fileName)
        if (!imageFile.exists()) {
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            val prediction = classifier.classify(bitmap)
            uploadImageToServer(imageFile, prediction)
        }
    }

    private fun saveImageUri(uri: Uri, fileName: String) {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(storageDir, fileName)
        if (!imageFile.exists()) {
            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                FileOutputStream(imageFile).use { output ->
                    input.copyTo(output)
                }
            }
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val prediction = classifier.classify(bitmap)
            uploadImageToServer(imageFile, prediction)
        }
    }

    private fun uploadImageToServer(file: File, prediction: String) {
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val predictionPart = okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), prediction)

        val call = ApiClient.getImageService(this).uploadImage(imagePart, predictionPart)
        ApiClient.tagCall(call, this, OnUnauthorizedCallback { ctx: Context ->
            ctx.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().clear().apply()
            ctx.startActivity(Intent(ctx, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            (ctx as? ComponentActivity)?.finish()
        }).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { uploadResponse ->
                        Log.d("HomeActivity", "Image uploaded with ID: ${uploadResponse.id}, Prediction: $prediction")
                        fetchUserImages()
                    }
                } else {
                    Log.e("HomeActivity", "Upload failed: ${response.errorBody()?.string()}")
                    Toast.makeText(this@HomeActivity, "Upload failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Log.e("HomeActivity", "Upload failed: ${t.message}")
                Toast.makeText(this@HomeActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onWardrobeClick: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    clothingImages: List<Pair<String, String>>,
    onRecommendationClick: (String, String) -> Unit,
    onDeleteImage: (String) -> Unit,
    onMarketplaceClick: () -> Unit
) {
    var isMale by remember { mutableStateOf(true) }
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    // Updated colors based on your preference (#F83758) and theme
    val primaryColor = Color(0xFFF83758) // Main color from your request
    val backgroundColor = Color.White // White background for a clean look
    val textColor = Color.Black // Black for readability
    val secondaryColor = Color(0xFFC2C8DA) // Light gray for borders and accents
    val whiteColor = Color.White // Explicitly defined for clarity

    fun deleteImage(imageId: String) {
        val call = ApiClient.getImageService(context).deleteImage(imageId)
        ApiClient.tagCall(call, context, OnUnauthorizedCallback { ctx: Context ->
            ctx.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().clear().apply()
            ctx.startActivity(Intent(ctx, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            (ctx as? ComponentActivity)?.finish()
        }).enqueue(object : Callback<DeleteResponse> {
            override fun onResponse(call: Call<DeleteResponse>, response: Response<DeleteResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { deleteResponse ->
                        if (deleteResponse.success) {
                            onDeleteImage(imageId)
                            Toast.makeText(context, "Image deleted successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to delete image", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to delete image", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                Toast.makeText(context, "Failed to delete image", Toast.LENGTH_SHORT).show()
            }
        })
    }

    showDeleteDialog?.let { imageId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Image", color = textColor, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently delete this image?", color = textColor) },
            confirmButton = {
                Button(
                    onClick = {
                        deleteImage(imageId)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = whiteColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = textColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = whiteColor,
            shape = RoundedCornerShape(16.dp)
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gender",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isMale) "Men" else "Women",
                        fontSize = 18.sp,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Switch(
                        checked = isMale,
                        onCheckedChange = { isMale = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = primaryColor,
                            checkedTrackColor = secondaryColor,
                            uncheckedThumbColor = textColor.copy(alpha = 0.5f),
                            uncheckedTrackColor = secondaryColor.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = whiteColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Outfits Calendar",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Today, ${getCurrentDate()}",
                        fontSize = 20.sp,
                        color = textColor
                    )
                }
            }
        }

        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Outfits",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        IconButton(onClick = onWardrobeClick) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_add),
                                contentDescription = "Add Outfit",
                                modifier = Modifier.size(36.dp),
                                tint = primaryColor
                            )
                        }
                        IconButton(onClick = onCameraClick) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_camera),
                                contentDescription = "Take Photo",
                                modifier = Modifier.size(36.dp),
                                tint = primaryColor
                            )
                        }
                        IconButton(onClick = onGalleryClick) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                                contentDescription = "Upload Photo",
                                modifier = Modifier.size(36.dp),
                                tint = primaryColor
                            )
                        }
                        IconButton(onClick = onMarketplaceClick) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_slideshow),
                                contentDescription = "Marketplace",
                                modifier = Modifier.size(36.dp),
                                tint = primaryColor
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(clothingImages.size) { index ->
                        val (path, prediction) = clothingImages[index]
                        OutfitCard(
                            imageSource = path,
                            prediction = prediction,
                            onDeleteClick = { showDeleteDialog = path },
                            primaryColor = primaryColor,
                            textColor = textColor,
                            secondaryColor = secondaryColor,
                            whiteColor = whiteColor
                        )
                    }
                    if (clothingImages.isEmpty()) {
                        item {
                            OutfitCard(
                                imageSource = "No Items",
                                prediction = "Unknown",
                                onDeleteClick = {},
                                primaryColor = primaryColor,
                                textColor = textColor,
                                secondaryColor = secondaryColor,
                                whiteColor = whiteColor
                            )
                        }
                    }
                }
            }
        }

        item {
            Column {
                Text(
                    text = "Recommendations",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { onRecommendationClick("Casual", if (isMale) "Men" else "Women") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = whiteColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Casual", fontSize = 16.sp)
                    }
                    Button(
                        onClick = { onRecommendationClick("Formal", if (isMale) "Women" else "Men") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = whiteColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Formal", fontSize = 16.sp)
                    }
                    Button(
                        onClick = { onRecommendationClick("Sporty", if (isMale) "Men" else "Women") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = whiteColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Sporty", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun OutfitCard(
    imageSource: String,
    prediction: String,
    onDeleteClick: (String) -> Unit,
    primaryColor: Color,
    textColor: Color,
    secondaryColor: Color,
    whiteColor: Color
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = whiteColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                val context = LocalContext.current
                val imageUrl = "http://10.0.2.2:4000/api/images/$imageSource"
                val token = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                    .getString("jwt_token", null)
                val painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(imageUrl)
                        .error(android.R.drawable.ic_menu_gallery)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .apply {
                            if (token != null) {
                                addHeader("Authorization", "Bearer $token")
                            }
                        }
                        .build()
                )
                Image(
                    painter = painter,
                    contentDescription = prediction,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, secondaryColor, RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = prediction,
                    fontSize = 16.sp,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium
                )
            }
            IconButton(
                onClick = { onDeleteClick(imageSource) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(28.dp)
                    .border(1.dp, primaryColor, RoundedCornerShape(50))
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "Delete Image",
                    tint = primaryColor
                )
            }
        }
    }
}

// Function to get the current date
fun getCurrentDate(): String {
    val calendar = Calendar.getInstance()
    val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    return "Today, $day $month"
}
package tn.esprit.outfitaura2.viewmodels

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.outfitaura2.LoginActivity
import tn.esprit.outfitaura2.models.DeleteResponse
import tn.esprit.outfitaura2.network.ApiClient
import tn.esprit.outfitaura2.network.OnUnauthorizedCallback
import tn.esprit.outfitaura2.ui.theme.OutfitAura2Theme

class WardrobeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val clothingImageIds = intent.getStringArrayListExtra("clothingImageIds") ?: arrayListOf()
        val clothingPredictions = intent.getStringArrayListExtra("clothingPredictions") ?: arrayListOf()
        val clothingImages = clothingImageIds.zip(clothingPredictions).map { Pair(it.first, it.second) }

        setContent {
            OutfitAura2Theme {
                val clothingImagesState = remember { mutableStateOf(clothingImages) }
                WardrobeScreen(
                    clothingImages = clothingImagesState.value,
                    onBackClick = { finish() },
                    onDeleteClick = { imageId ->
                        clothingImagesState.value = clothingImagesState.value.filter { it.first != imageId }
                    }
                )
            }
        }
    }
}

@Composable
fun WardrobeScreen(
    clothingImages: List<Pair<String, String>>,
    onBackClick: () -> Unit,
    onDeleteClick: (String) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

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
                            onDeleteClick(imageId)
                            Log.d("WardrobeScreen", "Image deleted: $imageId")
                            Toast.makeText(context, "Image deleted successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e("WardrobeScreen", "Delete failed: ${deleteResponse.error}")
                            Toast.makeText(context, "Failed to delete image", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("WardrobeScreen", "Delete failed: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Failed to delete image", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                Log.e("WardrobeScreen", "Delete failed: ${t.message}")
                Toast.makeText(context, "Failed to delete image", Toast.LENGTH_SHORT).show()
            }
        })
    }

    showDeleteDialog?.let { imageId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Image", color = Color.Black, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently delete this image?", color = Color.Black) },
            confirmButton = {
                Button(
                    onClick = {
                        deleteImage(imageId)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF83758), contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2C8DA), contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_revert),
                    contentDescription = "Back",
                    modifier = Modifier.size(36.dp),
                    tint = Color(0xFFF83758)
                )
            }
            Text(
                text = "My Wardrobe",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF83758),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(clothingImages.size) { index ->
                val (imageId, prediction) = clothingImages[index]
                if (imageId.isEmpty()) {
                    Log.w("WardrobeScreen", "Skipping empty imageId for prediction: $prediction")
                    return@items
                }
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, Color(0xFFC2C8DA), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        val imageUrl = "http://10.0.2.2:4000/api/images/$imageId"
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
                                .listener(
                                    onError = { _, result ->
                                        Log.e("WardrobeScreen", "Failed to load image $imageUrl: ${result.throwable.message}")
                                    },
                                    onSuccess = { _, _ ->
                                        Log.d("WardrobeScreen", "Successfully loaded image $imageUrl")
                                    }
                                )
                                .build()
                        )
                        Image(
                            painter = painter,
                            contentDescription = "Clothing Item",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Type: $prediction",
                            fontSize = 14.sp,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(
                        onClick = { showDeleteDialog = imageId },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(28.dp)
                            .border(1.dp, Color(0xFFF83758), RoundedCornerShape(50))
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = "Delete Image",
                            tint = Color(0xFFF83758),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            if (clothingImages.isEmpty()) {
                items(4) {
                    Column(
                        modifier = Modifier
                            .width(140.dp)
                            .height(180.dp)
                            .border(2.dp, Color(0xFFC2C8DA), RoundedCornerShape(16.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = "Placeholder",
                            modifier = Modifier.size(80.dp),
                            tint = Color(0xFFC2C8DA)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WardrobeScreenPreview() {
    OutfitAura2Theme {
        WardrobeScreen(
            clothingImages = listOf(Pair("id", "t-shirt")),
            onBackClick = {},
            onDeleteClick = {}
        )
    }
}
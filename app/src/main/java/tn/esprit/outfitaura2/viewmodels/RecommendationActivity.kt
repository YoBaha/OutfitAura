package tn.esprit.outfitaura2.viewmodels

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import tn.esprit.outfitaura2.ui.theme.OutfitAura2Theme

class RecommendationActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageIds = intent.getStringArrayListExtra("clothingImageIds") ?: emptyList()
        val clothingPredictions = intent.getStringArrayListExtra("clothingPredictions") ?: emptyList()
        val recommendationType = intent.getStringExtra("recommendationType") ?: "Casual"
        val gender = intent.getStringExtra("gender") ?: "Men"

        Log.d("RecommendationActivity", "imageIds: $imageIds")
        Log.d("RecommendationActivity", "clothingPredictions: $clothingPredictions")
        Log.d("RecommendationActivity", "recommendationType: $recommendationType")
        Log.d("RecommendationActivity", "gender: $gender")

        val combinedClothing = imageIds.zip(clothingPredictions + List(imageIds.size - clothingPredictions.size) { "" })
            .take(imageIds.size)
        Log.d("RecommendationActivity", "combinedClothing: $combinedClothing")

        setContent {
            OutfitAura2Theme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Outfit Recommendation") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        painter = painterResource(id = android.R.drawable.ic_menu_revert),
                                        contentDescription = "Back",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    RecommendationScreen(
                        modifier = Modifier.padding(innerPadding),
                        clothingImages = combinedClothing,
                        recommendationType = recommendationType,
                        gender = gender
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendationScreen(
    modifier: Modifier = Modifier,
    clothingImages: List<Pair<String, String>>,
    recommendationType: String,
    gender: String
) {
    var currentIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val recommendations = when (gender) {
        "Women" -> mapOf(
            "Formal" to listOf("suit-women" to "Suit", "dress" to "Dress", "skirt" to "Skirt", "pants" to "Pants"),
            "Casual" to listOf("t-shirt" to "T-Shirt", "pants" to "Pants", "sneakers" to "Sneakers", "Hoodie" to "Hoodie", "longsleeve" to "Longsleeve", "outwear" to "Outwear", "hat" to "Hat"),
            "Sporty" to listOf("athleisure-women" to "Athleisure", "shorts" to "Shorts", "sneakers" to "Sneakers", "Hoodie" to "Hoodie")
        )
        "Men" -> mapOf(
            "Formal" to listOf("suit-men" to "Suit", "shirt" to "Shirt", "shoes" to "Shoes", "pants" to "Pants"),
            "Casual" to listOf("t-shirt" to "T-Shirt", "pants" to "Pants", "sneakers" to "Sneakers", "Hoodie" to "Hoodie", "longsleeve" to "Longsleeve", "outwear" to "Outwear", "hat" to "Hat"),
            "Sporty" to listOf("athleisure-men" to "Athleisure", "shorts" to "Shorts", "sneakers" to "Sneakers", "Hoodie" to "Hoodie")
        )
        else -> emptyMap()
    }

    val labelSynonyms = when (gender) {
        "Women" -> mapOf(
            "suit-women" to listOf("suit-women"), "dress" to listOf("dress"), "skirt" to listOf("skirt"),
            "pants" to listOf("pants", "shorts"), "t-shirt" to listOf("t-shirt", "shirt"), "Hoodie" to listOf("Hoodie"),
            "longsleeve" to listOf("longsleeve"), "outwear" to listOf("outwear"), "hat" to listOf("hat"),
            "athleisure-women" to listOf("athleisure-women"), "shorts" to listOf("shorts"), "sneakers" to listOf("sneakers", "shoes")
        )
        "Men" -> mapOf(
            "suit-men" to listOf("suit-men"), "shirt" to listOf("shirt"), "shoes" to listOf("shoes"),
            "pants" to listOf("pants", "shorts"), "t-shirt" to listOf("t-shirt"), "Hoodie" to listOf("Hoodie"),
            "longsleeve" to listOf("longsleeve"), "outwear" to listOf("outwear"), "hat" to listOf("hat"),
            "athleisure-men" to listOf("athleisure-men"), "shorts" to listOf("shorts"), "sneakers" to listOf("sneakers", "shoes")
        )
        else -> emptyMap()
    }

    val recommendedLayers = recommendations[recommendationType] ?: emptyList()
    val filteredItems = clothingImages.filter { item ->
        recommendedLayers.any { (type, _) ->
            labelSynonyms[type]?.contains(item.second.lowercase()) == true
        }
    }
    val groupedItems = filteredItems.groupBy { it.second.lowercase() }

    Log.d("RecommendationScreen", "clothingImages: $clothingImages")
    Log.d("RecommendationScreen", "filteredItems: $filteredItems")
    Log.d("RecommendationScreen", "groupedItems: $groupedItems")
    Log.d("RecommendationScreen", "recommendedLayers: $recommendedLayers")
    Log.d("RecommendationScreen", "labelSynonyms: $labelSynonyms")

    val combinationSizes = recommendedLayers.map { (type, _) ->
        labelSynonyms[type]?.sumOf { synonym -> groupedItems[synonym.lowercase()]?.size ?: 0 } ?: 0
    }
    val adjustedCombinationSizes = combinationSizes.map { if (it == 0) 1 else it }
    val totalCombinations = adjustedCombinationSizes.reduceOrNull { a, b -> a * b } ?: 1
    Log.d("RecommendationScreen", "combinationSizes: $combinationSizes")
    Log.d("RecommendationScreen", "adjustedCombinationSizes: $adjustedCombinationSizes")
    Log.d("RecommendationScreen", "totalCombinations: $totalCombinations")

    val currentCombination = if (filteredItems.isNotEmpty()) {
        recommendedLayers.mapIndexed { index, (type, _) ->
            val availableItems = labelSynonyms[type]?.flatMap { synonym ->
                groupedItems[synonym.lowercase()] ?: emptyList()
            } ?: emptyList()
            if (availableItems.isNotEmpty()) {
                val itemIndex = (currentIndex / adjustedCombinationSizes.slice(0 until index).fold(1) { acc, size -> acc * size }) % availableItems.size
                availableItems[itemIndex].first
            } else {
                ""
            }
        }
    } else {
        List(recommendedLayers.size) { "" }
    }
    Log.d("RecommendationScreen", "currentCombination: $currentCombination")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp), // Increased spacing
            contentPadding = PaddingValues(bottom = 80.dp) // Space for button
        ) {
            item {
                Text(
                    text = "Recommended $recommendationType Outfit",
                    fontSize = 28.sp, // Increased font size
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF83758),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            if (filteredItems.isEmpty()) {
                item {
                    Text(
                        text = "No matching items found for $recommendationType outfit. Upload more items in Home!",
                        fontSize = 16.sp,
                        color = Color(0xFF000000),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                itemsIndexed(recommendedLayers) { index, (type, layerName) ->
                    val imageId = currentCombination.getOrNull(index) ?: ""
                    val imageUrl = if (imageId.isNotEmpty()) "http://10.0.2.2:4000/api/images/$imageId" else null
                    Log.d("RecommendationScreen", "Loading image for $layerName: $imageUrl")

                    Card(
                        modifier = Modifier
                            .width(140.dp) // Increased width
                            .height(180.dp) // Increased height
                            .clip(RoundedCornerShape(16.dp)), // Larger rounded corners
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), // Increased elevation
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(context)
                                        .data(imageUrl)
                                        .error(android.R.drawable.ic_menu_gallery)
                                        .placeholder(android.R.drawable.ic_menu_gallery)
                                        .listener(
                                            onError = { _, result ->
                                                Log.e("RecommendationScreen", "Failed to load image $imageUrl: ${result.throwable.message}")
                                            },
                                            onSuccess = { _, _ ->
                                                Log.d("RecommendationScreen", "Successfully loaded image $imageUrl")
                                            }
                                        )
                                        .build()
                                ),
                                contentDescription = "$layerName Image",
                                modifier = Modifier
                                    .size(100.dp) // Increased image size
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (imageId.isEmpty()) "No $layerName available" else layerName,
                                fontSize = 14.sp,
                                color = Color.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
        if (filteredItems.isNotEmpty()) {
            Button(
                onClick = {
                    currentIndex = (currentIndex + 1) % totalCombinations
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp) // Consistent button height
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF83758), contentColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Change Outfit", fontSize = 16.sp)
            }
        }
    }
}
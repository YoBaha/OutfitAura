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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.outfitaura2.LoginActivity
import tn.esprit.outfitaura2.models.CategoryResponse
import tn.esprit.outfitaura2.models.Product
import tn.esprit.outfitaura2.models.ProductResponse
import tn.esprit.outfitaura2.network.ApiClient
import tn.esprit.outfitaura2.network.OnUnauthorizedCallback
import tn.esprit.outfitaura2.ui.theme.OutfitAura2Theme

class MarketplaceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OutfitAura2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MarketplaceScreen(
                        modifier = Modifier.padding(innerPadding),
                        onBackClick = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun MarketplaceScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    val context = LocalContext.current

    // Clothing-related keywords for filtering
    val clothingKeywords = listOf(
        "classic", "Classic", "Majestic"
    )
    val nonClothingKeywords = listOf("watch")

    LaunchedEffect(Unit) {
        // Fetch categories for debugging
        val categoryCall = ApiClient.getMarketplaceService(context).getCategories()
        ApiClient.tagCall(categoryCall, context, OnUnauthorizedCallback { ctx: Context ->
            ctx.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().clear().apply()
            ctx.startActivity(Intent(ctx, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            (ctx as? ComponentActivity)?.finish()
        }).enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { categoryResponse ->
                        if (categoryResponse.success) {
                            Log.d("MarketplaceScreen", "Categories: ${categoryResponse.categories}")
                        } else {
                            Log.e("MarketplaceScreen", "Failed to fetch categories: ${categoryResponse.error}")
                        }
                    }
                } else {
                    Log.e("MarketplaceScreen", "Category fetch failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                Log.e("MarketplaceScreen", "Category fetch failed: ${t.message}")
            }
        })

        // Fetch clothing products
        val productCall = ApiClient.getMarketplaceService(context).getProducts(
            offset = 0,
            limit = 10,
            categorySlug = "clothes"
        )
        ApiClient.tagCall(productCall, context, OnUnauthorizedCallback { ctx: Context ->
            ctx.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().clear().apply()
            ctx.startActivity(Intent(ctx, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            (ctx as? ComponentActivity)?.finish()
        }).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { productResponse ->
                        if (productResponse.success) {
                            // Filter products by title
                            products = productResponse.products.filter { product ->
                                val titleLower = product.title.lowercase()
                                val isClothing = clothingKeywords.any { titleLower.contains(it) } ||
                                        !nonClothingKeywords.any { titleLower.contains(it) }
                                if (!isClothing) {
                                    Log.w("MarketplaceScreen", "Filtered out non-clothing product: ${product.title}")
                                }
                                isClothing
                            }
                            Log.d("MarketplaceScreen", "Fetched ${products.size} clothing products")
                        } else {
                            Log.e("MarketplaceScreen", "Failed to fetch products: ${productResponse.error}")
                            Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("MarketplaceScreen", "Fetch failed: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Log.e("MarketplaceScreen", "Fetch failed: ${t.message}")
                Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show()
            }
        })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_revert),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "Marketplace - Clothing",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(products.size) { index ->
                val product = products[index]
                ProductCard(
                    product = product,
                    onAddToCart = {
                        Toast.makeText(context, "${product.title} added to cart", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            if (products.isEmpty()) {
                items(4) {
                    Column(
                        modifier = Modifier
                            .width(120.dp)
                            .height(180.dp)
                            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = "Placeholder",
                            modifier = Modifier.size(80.dp)
                        )
                        Text(
                            text = "Loading...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            val context = LocalContext.current
            val imageUrl = product.images.firstOrNull() ?: "https://placehold.co/600x400"
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(imageUrl)
                    .error(android.R.drawable.ic_menu_gallery)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .build()
            )
            Image(
                painter = painter,
                contentDescription = product.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
            Text(
                text = product.title,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$${product.price}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = onAddToCart,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(
                    text = "Add to Cart",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}
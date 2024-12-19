package tn.esprit.outfitaura2.ui.theme

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

// StyleButton composable
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

// ImageGrid composable
@Composable
fun ImageGrid(filteredImages: List<Pair<Bitmap, String>>) {
    if (filteredImages.isNotEmpty()) {
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

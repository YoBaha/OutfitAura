package tn.esprit.outfitaura2.viewmodels

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import tn.esprit.outfitaura2.R
import tn.esprit.outfitaura2.ui.theme.OutfitAura2Theme
import java.io.File

class HomeActivity : ComponentActivity() {

    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register the camera launcher
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                showToast("Photo captured successfully!")
            } else {
                showToast("Failed to capture photo.")
            }
        }

        setContent {
            OutfitAura2Theme {
                HomeScreen(onCameraClick = { checkPermissions() })
            }
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                CAMERA_REQUEST_CODE
            )
        } else {
            capturePhoto()
        }
    }

    private fun capturePhoto() {
        val photoFile = File.createTempFile("photo_", ".jpg", cacheDir)
        photoUri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            photoFile
        )
        cameraLauncher.launch(photoUri)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val CAMERA_REQUEST_CODE = 1001
    }
}

@Composable
fun HomeScreen(onCameraClick: () -> Unit) {
    // Background Image
    val backgroundImage: Painter = painterResource(id = R.drawable.bg2)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay UI elements
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome to Home", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { onCameraClick() }) {
                Text("Open Camera")
            }
        }
    }
}

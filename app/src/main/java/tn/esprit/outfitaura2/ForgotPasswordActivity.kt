package tn.esprit.outfitaura2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tn.esprit.outfitaura2.ui.theme.OutfitAura2Theme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.outfitaura2.network.ApiClient
import android.app.Activity
import androidx.compose.ui.platform.LocalContext

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OutfitAura2Theme {
                ForgotPasswordScreen()
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen() {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var responseMessage by remember { mutableStateOf("") }

    // Back Button
    val context = LocalContext.current

    Button(
        onClick = {
            val activity = context as? Activity
            activity?.onBackPressed()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Back to Login")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Forgot Password", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))

        // Email text field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        }

        if (responseMessage.isNotEmpty()) {
            Text(text = responseMessage, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Submit Button
        Button(
            onClick = {
                if (email.isNotEmpty()) {
                    isLoading = true
                    ApiClient.authService.forgotPassword(email).enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            isLoading = false
                            if (response.isSuccessful) {
                                responseMessage = "Check your email for password reset instructions."
                            } else {
                                responseMessage = "Error: ${response.message()}"
                            }
                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            isLoading = false
                            responseMessage = "Request failed: ${t.message}"
                        }
                    })
                } else {
                    responseMessage = "Please enter a valid email address."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    OutfitAura2Theme {
        ForgotPasswordScreen()
    }
}

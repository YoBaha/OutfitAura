package tn.esprit.outfitaura2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.outfitaura2.models.ResetPasswordRequest
import tn.esprit.outfitaura2.models.ResetPasswordResponse
import tn.esprit.outfitaura2.network.ApiClient
import tn.esprit.outfitaura2.ui.theme.OutfitAura2Theme

class ResetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = intent.getStringExtra("token") ?: ""
        setContent {
            OutfitAura2Theme {
                ResetPasswordScreen(
                    token = token,
                    onSubmit = { password -> submitResetPassword(token, password) },
                    showToast = { message -> showToast(message) },
                    onBackToLogin = { navigateToLoginActivity() }
                )
            }
        }
    }

    private fun submitResetPassword(token: String, password: String) {
        if (password.isEmpty()) {
            showToast("Please enter a new password")
            return
        }

        val request = ResetPasswordRequest(password)
        ApiClient.getAuthService(this).resetPassword(token, request).enqueue(object : Callback<ResetPasswordResponse> {
            override fun onResponse(call: Call<ResetPasswordResponse>, response: Response<ResetPasswordResponse>) {
                if (response.isSuccessful) {
                    val resetResponse = response.body()
                    if (resetResponse?.success == true) {
                        showToast(resetResponse.message ?: "Password reset successfully")
                        navigateToLoginActivity()
                    } else {
                        showToast(resetResponse?.error ?: "Error resetting password")
                    }
                } else {
                    showToast("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                showToast("Network error: ${t.message}")
            }
        })
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ResetPasswordScreen(
    token: String,
    onSubmit: (String) -> Unit,
    showToast: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (password.isEmpty()) {
                    showToast("Please enter a new password")
                    return@Button
                }
                isLoading = true
                onSubmit(password)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Reset Password")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { onBackToLogin() }) {
            Text("Back to Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResetPasswordScreenPreview() {
    OutfitAura2Theme {
        ResetPasswordScreen(
            token = "",
            onSubmit = {},
            showToast = {},
            onBackToLogin = {}
        )
    }
}
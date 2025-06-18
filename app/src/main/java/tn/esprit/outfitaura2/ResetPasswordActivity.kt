package tn.esprit.outfitaura2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.outfitaura2.models.ResetPasswordRequest
import tn.esprit.outfitaura2.models.ResetPasswordResponse
import tn.esprit.outfitaura2.network.ApiClient
import tn.esprit.outfitaura2.network.OnUnauthorizedCallback
import tn.esprit.outfitaura2.ui.theme.OutfitAura2Theme

class ResetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val email = intent.getStringExtra("email") ?: ""
        val code = intent.getStringExtra("code") ?: "" // Get code from intent
        setContent {
            OutfitAura2Theme {
                ResetPasswordScreen(
                    email = email,
                    code = code,
                    onSubmit = { password -> submitResetPassword(email, code, password) },
                    showToast = { message -> showToast(message) },
                    onBackToLogin = { navigateToLoginActivity() }
                )
            }
        }
    }

    private fun submitResetPassword(email: String, code: String, password: String) {
        if (password.isEmpty()) {
            showToast("Please enter a new password")
            return
        }

        val request = ResetPasswordRequest(email, code, password)
        val call = ApiClient.getAuthService(this).resetPassword(request)
        ApiClient.tagCall(call, this, OnUnauthorizedCallback { ctx ->
            ctx.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().clear().apply()
            ctx.startActivity(Intent(ctx, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            (ctx as? ComponentActivity)?.finish()
        }).enqueue(object : Callback<ResetPasswordResponse> {
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
    email: String,
    code: String,
    onSubmit: (String) -> Unit,
    showToast: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF83758)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("New Password", color = Color.Black) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color(0xFFC2C8DA),
                unfocusedBorderColor = Color(0xFFC2C8DA),
                cursorColor = Color(0xFFF83758)
            )
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
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF83758),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Reset Password", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { onBackToLogin() }) {
            Text("Back to Login", color = Color(0xFFF83758))
        }
    }
}
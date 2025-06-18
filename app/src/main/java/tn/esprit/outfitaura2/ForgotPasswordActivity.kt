package tn.esprit.outfitaura2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.outfitaura2.models.ForgotPasswordRequest
import tn.esprit.outfitaura2.models.ForgotPasswordResponse
import tn.esprit.outfitaura2.models.ResetPasswordRequest
import tn.esprit.outfitaura2.models.ResetPasswordResponse
import tn.esprit.outfitaura2.network.ApiClient
import tn.esprit.outfitaura2.network.OnUnauthorizedCallback
import tn.esprit.outfitaura2.ui.theme.OutfitAura2Theme
import tn.esprit.outfitaura2.ResetPasswordActivity
import tn.esprit.outfitaura2.models.VerifyCodeRequest
import tn.esprit.outfitaura2.models.VerifyCodeResponse

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OutfitAura2Theme {
                ForgotPasswordScreen(
                    onEmailSubmit = { email -> submitForgotPassword(email) },
                    onCodeSubmit = { email, code -> submitCode(email, code) }, // Pass submitCode
                    showToast = { message -> showToast(message) },
                    onBackToLogin = { navigateToLoginActivity() }
                )
            }
        }
    }

    private fun submitForgotPassword(email: String) {
        if (email.isEmpty()) {
            showToast("Please enter your email")
            return
        }

        val request = ForgotPasswordRequest(email)
        val call = ApiClient.getAuthService(this).forgotPassword(request)
        ApiClient.tagCall(call, this, OnUnauthorizedCallback { ctx ->
            ctx.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().clear().apply()
            ctx.startActivity(Intent(ctx, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            (ctx as? ComponentActivity)?.finish()
        }).enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                if (response.isSuccessful) {
                    val forgotResponse = response.body()
                    if (forgotResponse?.success == true) {
                        showToast(forgotResponse.message ?: "Password reset code sent")
                        // Stay on this screen to enter the code
                    } else {
                        showToast(forgotResponse?.error ?: "Failed to send reset email")
                    }
                } else {
                    showToast("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                showToast("Network error: ${t.message}")
            }
        })
    }

    private fun submitCode(email: String, code: String) {
        if (code.length != 4) {
            showToast("Please enter a 4-digit code")
            return
        }

        val request = VerifyCodeRequest(email, code) // New data class
        Log.d("ForgotPassword", "Submitting verify request: email=$email, code=$code")
        val call = ApiClient.getAuthService(this).verifyCode(request)
        ApiClient.tagCall(call, this, OnUnauthorizedCallback { ctx ->
            ctx.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().clear().apply()
            ctx.startActivity(Intent(ctx, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            (ctx as? ComponentActivity)?.finish()
        }).enqueue(object : Callback<VerifyCodeResponse> {
            override fun onResponse(call: Call<VerifyCodeResponse>, response: Response<VerifyCodeResponse>) {
                Log.d("ForgotPassword", "Response: code=${response.code()}, body=${response.body()}, error=${response.errorBody()?.string()}")
                if (response.isSuccessful) {
                    val verifyResponse = response.body()
                    if (verifyResponse?.success == true) {
                        showToast(verifyResponse.message ?: "Code verified")
                        val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                        intent.putExtra("email", email)
                        intent.putExtra("code", code)
                        startActivity(intent)
                        finish()
                    } else {
                        showToast(verifyResponse?.error ?: "Invalid or expired code")
                    }
                } else {
                    showToast("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<VerifyCodeResponse>, t: Throwable) {
                Log.e("ForgotPassword", "Failure: ${t.message}")
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
fun ForgotPasswordScreen(
    onEmailSubmit: (String) -> Unit,
    onCodeSubmit: (String, String) -> Unit,
    showToast: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isCodeStep by remember { mutableStateOf(false) }
    var code by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (!isCodeStep) "Forgot Password" else "Enter Code",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF83758)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!isCodeStep) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.Black) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
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
        } else {
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("4-Digit Code", color = Color.Black) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (!isLoading) {
                    if (!isCodeStep) {
                        if (email.isEmpty()) {
                            showToast("Please enter your email")
                            return@Button
                        }
                        isLoading = true
                        onEmailSubmit(email)
                        isLoading = false
                        isCodeStep = true
                    } else {
                        if (code.isEmpty()) {
                            showToast("Please enter the code")
                            return@Button
                        }
                        isLoading = true
                        onCodeSubmit(email, code)
                        isLoading = false
                    }
                }
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
                Text(if (!isCodeStep) "Submit" else "Verify Code", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { onBackToLogin() }) {
            Text("Back to Login", color = Color(0xFFF83758))
        }
    }
}
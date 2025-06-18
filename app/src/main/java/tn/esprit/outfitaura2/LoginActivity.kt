package tn.esprit.outfitaura2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.outfitaura2.models.LoginRequest
import tn.esprit.outfitaura2.models.LoginResponse
import tn.esprit.outfitaura2.network.ApiClient
import tn.esprit.outfitaura2.network.OnUnauthorizedCallback
import tn.esprit.outfitaura2.ui.theme.OutfitAura2Theme
import tn.esprit.outfitaura2.viewmodels.HomeActivity

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OutfitAura2Theme {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var isLoading by remember { mutableStateOf(false) }

                LoginUI(
                    email = email,
                    onEmailChange = { email = it },
                    password = password,
                    onPasswordChange = { password = it },
                    isLoading = isLoading,
                    onLoginClick = { email, password ->
                        isLoading = true
                        loginUser(email, password) { success ->
                            isLoading = false
                            if (success) navigateToHomeActivity()
                        }
                    },
                    showToast = { message -> showToast(message) },
                    navigateToSignUp = { navigateToSignUpActivity() },
                    navigateToForgotPassword = { navigateToForgotPasswordActivity() }
                )
            }
        }
    }

    private fun loginUser(email: String, password: String, onComplete: (Boolean) -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please enter both email and password")
            onComplete(false)
            return
        }

        val loginRequest = LoginRequest(email, password)
        val call = ApiClient.getAuthService(this).login(loginRequest)
        ApiClient.tagCall(call, this, OnUnauthorizedCallback { ctx: Context ->
            ctx.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().clear().apply()
            showToast("Session expired. Please log in again.")
            // Already in LoginActivity, so no need to navigate
        }).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.success == true) {
                        getSharedPreferences("auth_prefs", MODE_PRIVATE)
                            .edit()
                            .putString("jwt_token", loginResponse.token)
                            .apply()
                        showToast("Login Successful")
                        onComplete(true)
                    } else {
                        showToast(loginResponse?.error ?: "Invalid credentials")
                        onComplete(false)
                    }
                } else {
                    showToast("Error: ${response.message()}")
                    onComplete(false)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showToast("Network error: ${t.message}")
                onComplete(false)
            }
        })
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToSignUpActivity() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToForgotPasswordActivity() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun LoginUI(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onLoginClick: (String, String) -> Unit,
    showToast: (String) -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToForgotPassword: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg3),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.a),
                contentDescription = "App Logo",
                modifier = Modifier.size(250.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Log In",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email", color = Color.White) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (!isLoading) {
                        onLoginClick(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black
                    )
                } else {
                    Text("Log In")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = navigateToSignUp) {
                Text("Don't have an account? Sign Up", color = Color.White)
            }
            TextButton(onClick = navigateToForgotPassword) {
                Text("Forgot Password?", color = Color.White)
            }
        }
    }
}
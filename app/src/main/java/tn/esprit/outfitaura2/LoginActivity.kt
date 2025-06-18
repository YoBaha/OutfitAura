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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .background(Color.White)
    ) {
        // Removed background image for consistency with other screens
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.a),
                contentDescription = "App Logo",
                modifier = Modifier.size(200.dp) // Reduced size for better balance
            )
            Spacer(modifier = Modifier.height(32.dp)) // Increased spacing
            Text(
                text = "Log In",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF83758)
                )
            )
            Spacer(modifier = Modifier.height(32.dp)) // Increased spacing
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email", color = Color(0xFF000000)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
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
            Spacer(modifier = Modifier.height(20.dp)) // Increased spacing
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password", color = Color(0xFF000000)) },
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
            Spacer(modifier = Modifier.height(24.dp)) // Increased spacing
            Button(
                onClick = {
                    if (!isLoading) {
                        onLoginClick(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp), // Consistent button height
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF83758),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Log In", fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = navigateToSignUp) {
                Text("Don't have an account? Sign Up", color = Color(0xFFF83758))
            }
            TextButton(onClick = navigateToForgotPassword) {
                Text("Forgot Password?", color = Color(0xFFF83758))
            }
        }
    }
}
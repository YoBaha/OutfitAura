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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.outfitaura2.models.SignUpRequest
import tn.esprit.outfitaura2.models.SignUpResponse
import tn.esprit.outfitaura2.network.ApiClient
import tn.esprit.outfitaura2.network.OnUnauthorizedCallback
import tn.esprit.outfitaura2.ui.theme.OutfitAura2Theme

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OutfitAura2Theme {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var isLoading by remember { mutableStateOf(false) }

                SignUpUI(
                    email = email,
                    onEmailChange = { email = it },
                    password = password,
                    onPasswordChange = { password = it },
                    isLoading = isLoading,
                    onSignUpClick = { email, password ->
                        isLoading = true
                        signUpUser(email, password) { success ->
                            isLoading = false
                            if (success) navigateToLoginActivity()
                        }
                    },
                    showToast = { message -> showToast(message) },
                    navigateToLogin = { navigateToLoginActivity() }
                )
            }
        }
    }

    private fun signUpUser(email: String, password: String, onComplete: (Boolean) -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please enter both email and password")
            onComplete(false)
            return
        }

        val signUpRequest = SignUpRequest(email, password)
        val call = ApiClient.getAuthService(this).register(signUpRequest)
        ApiClient.tagCall(call, this, OnUnauthorizedCallback { ctx: Context ->
            ctx.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().clear().apply()
            showToast("Session issue. Please try again.")
        }).enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                if (response.isSuccessful) {
                    val signUpResponse = response.body()
                    if (signUpResponse?.success == true) {
                        showToast(signUpResponse.message ?: "Sign Up Successful")
                        onComplete(true)
                    } else {
                        showToast(signUpResponse?.error ?: "Email already registered")
                        onComplete(false)
                    }
                } else {
                    showToast("Error: ${response.message()}")
                    onComplete(false)
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                showToast("Network error: ${t.message}")
                onComplete(false)
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
fun SignUpUI(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onSignUpClick: (String, String) -> Unit,
    showToast: (String) -> Unit,
    navigateToLogin: () -> Unit
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
                text = "Sign Up",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF83758)
                )
            )
            Spacer(modifier = Modifier.height(32.dp)) // Increased spacing
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email", color = Color.Black) },
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
                label = { Text("Password", color = Color.Black) },
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
                        onSignUpClick(email, password)
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
                    Text("Sign Up", fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navigateToLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp), // Consistent button height
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC2C8DA),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Already have an account? Log In", fontSize = 14.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpUIPreview() {
    OutfitAura2Theme {
        SignUpUI(
            email = "",
            onEmailChange = {},
            password = "",
            onPasswordChange = {},
            isLoading = false,
            onSignUpClick = { _, _ -> },
            showToast = {},
            navigateToLogin = {}
        )
    }
}
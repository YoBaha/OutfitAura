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
    import tn.esprit.outfitaura2.ui.theme.OutfitAura2Theme
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response
    import tn.esprit.outfitaura2.network.ApiClient
    import androidx.compose.foundation.Image
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.res.painterResource
    import tn.esprit.outfitaura2.models.LoginRequest
    import tn.esprit.outfitaura2.models.LoginResponse
    import tn.esprit.outfitaura2.network.User
    import tn.esprit.outfitaura2.ui.theme.WhiteColor
    import tn.esprit.outfitaura2.view.SessionManager
    import tn.esprit.outfitaura2.viewmodels.HomeActivity

    class LoginActivity : ComponentActivity() {

        private lateinit var sessionManager: SessionManager

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            sessionManager = SessionManager(this)  // Initialize session manager

            setContent {
                OutfitAura2Theme {
                    LoginScreen(
                        onLoginClick = { email, password -> loginUser(email, password) },
                        showToast = { message -> showToast(message) },
                        onSignUpClick = { navigateToSignUpActivity() },
                        onForgotPasswordClick = { navigateToForgotPasswordActivity() }
                    )
                }
            }
        }

        private fun loginUser(email: String, password: String) {
            if (email.isEmpty() || password.isEmpty()) {
                showToast("Please enter both email and password")
                return
            }

            val loginRequest = LoginRequest(email, password)

            ApiClient.authService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse?.success == true) {
                            val user = User(email)  // Assuming login response contains user ID
                            sessionManager.saveUser(user)  // Save user session
                            showToast("Login Successful")
                            navigateToHomeActivity()
                        } else {
                            showToast("Invalid login credentials")
                        }
                    } else {
                        showToast("Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    showToast("Network error: ${t.message}")
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
    fun LoginScreen(
        onLoginClick: (String, String) -> Unit,
        showToast: (String) -> Unit,
        onSignUpClick: () -> Unit,
        onForgotPasswordClick: () -> Unit
    ) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg3),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Login Form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo Image
                Image(
                    painter = painterResource(id = R.drawable.a),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(240.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Title Text
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.headlineLarge.copy(color = WhiteColor) // White text color for title
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Email TextField
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = WhiteColor) }, // White label
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = WhiteColor, // White label when focused
                        unfocusedLabelColor = WhiteColor, // White label when not focused
                        focusedTextColor = WhiteColor, // White text color when focused
                        unfocusedTextColor = WhiteColor, // White text color when not focused
                        cursorColor = WhiteColor, // White cursor
                        focusedBorderColor = WhiteColor, // White border when focused
                        unfocusedBorderColor = WhiteColor.copy(alpha = 0.5f) // Lighter border when unfocused
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password TextField
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = WhiteColor) }, // White label
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = WhiteColor, // White label when focused
                        unfocusedLabelColor = WhiteColor, // White label when not focused
                        focusedTextColor = WhiteColor, // White text color when focused
                        unfocusedTextColor = WhiteColor, // White text color when not focused
                        cursorColor = WhiteColor, // White cursor
                        focusedBorderColor = WhiteColor, // White border when focused
                        unfocusedBorderColor = WhiteColor.copy(alpha = 0.5f) // Lighter border when unfocused
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Login Button
                Button(
                    onClick = {
                        if (email.isEmpty() || password.isEmpty()) {
                            showToast("Please enter both email and password")
                            return@Button
                        }
                        isLoading = true
                        onLoginClick(email, password)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Login", color = WhiteColor) // White text color for button
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sign Up Button
                Button(
                    onClick = { onSignUpClick() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Don't have an account? Sign Up", color = WhiteColor) // White text for button
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Forgot Password Button
                TextButton(onClick = { onForgotPasswordClick() }) {
                    Text("Forgot Password?", color = WhiteColor) // White text for forgot password
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun LoginScreenPreview() {
        OutfitAura2Theme {
            LoginScreen(onLoginClick = { _, _ -> }, showToast = {}, onSignUpClick = {}, onForgotPasswordClick = {})
        }
    }

package tn.esprit.outfitaura2.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import tn.esprit.outfitaura2.models.ForgotPasswordRequest
import tn.esprit.outfitaura2.models.LoginRequest
import tn.esprit.outfitaura2.models.LoginResponse
import tn.esprit.outfitaura2.models.ResetPasswordRequest
import tn.esprit.outfitaura2.models.ResetPasswordResponse
import tn.esprit.outfitaura2.models.SignUpRequest
import tn.esprit.outfitaura2.models.SignUpResponse

data class User(
    val id: Int,
    val email: String
)

interface AuthService {
    @POST("user/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("user/register")
    fun register(@Body request: SignUpRequest): Call<SignUpResponse>

    // Modified to accept the email directly, no need for ForgotPasswordRequest
    @POST("user/forgot-password")
    fun forgotPassword(@Body email: String): Call<String> // Accepts email as a simple string

    @POST("user/reset-password")
    fun resetPassword(@Body request: ResetPasswordRequest): Call<ResetPasswordResponse>
}

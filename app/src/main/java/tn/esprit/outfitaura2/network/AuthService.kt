package tn.esprit.outfitaura2.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import tn.esprit.outfitaura2.models.ForgotPasswordRequest
import tn.esprit.outfitaura2.models.ForgotPasswordResponse
import tn.esprit.outfitaura2.models.LoginRequest
import tn.esprit.outfitaura2.models.LoginResponse
import tn.esprit.outfitaura2.models.ResetPasswordRequest
import tn.esprit.outfitaura2.models.ResetPasswordResponse
import tn.esprit.outfitaura2.models.SignUpRequest
import tn.esprit.outfitaura2.models.SignUpResponse
import tn.esprit.outfitaura2.models.VerifyCodeRequest
import tn.esprit.outfitaura2.models.VerifyCodeResponse

data class User(
    val email: String
)

interface AuthService {
    @POST("api/user/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/user/register")
    fun register(@Body request: SignUpRequest): Call<SignUpResponse>

    @POST("api/user/forgot-password")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    @POST("api/user/verify-code")
    fun verifyCode(@Body request: VerifyCodeRequest): Call<VerifyCodeResponse>

    @POST("api/user/reset-password")
    fun resetPassword(@Body request: ResetPasswordRequest): Call<ResetPasswordResponse>
}
package tn.esprit.outfitaura2.models

data class VerifyCodeRequest(
    val email: String,
    val code: String
)

data class VerifyCodeResponse(
    val success: Boolean,
    val message: String?,
    val error: String?
)
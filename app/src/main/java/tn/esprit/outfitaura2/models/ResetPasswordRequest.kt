package tn.esprit.outfitaura2.models

data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val password: String
)
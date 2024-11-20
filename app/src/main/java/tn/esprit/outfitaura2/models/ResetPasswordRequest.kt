package tn.esprit.outfitaura2.models

data class ResetPasswordRequest(
    val resetToken: String,
    val newPassword: String
)

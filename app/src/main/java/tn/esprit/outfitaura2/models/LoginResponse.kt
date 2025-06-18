package tn.esprit.outfitaura2.models

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val user: User?,
    val error: String?
)

data class User(
    val email: String
)
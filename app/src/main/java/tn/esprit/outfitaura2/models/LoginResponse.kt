package tn.esprit.outfitaura2.models

import tn.esprit.outfitaura2.network.User


data class LoginResponse(
    val success: Boolean,
    val user: User?
)
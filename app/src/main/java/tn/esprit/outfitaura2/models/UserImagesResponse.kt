package tn.esprit.outfitaura2.models

data class UserImagesResponse(
    val success: Boolean,
    val images: List<UserImage>,
    val error: String?
)

data class UserImage(
    val id: String,
    val filename: String,
    val prediction: String
)
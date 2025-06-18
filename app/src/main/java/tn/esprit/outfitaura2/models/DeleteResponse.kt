package tn.esprit.outfitaura2.models

data class DeleteResponse(
    val success: Boolean,
    val message: String?,
    val error: String?
)
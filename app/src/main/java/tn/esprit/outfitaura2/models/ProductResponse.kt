package tn.esprit.outfitaura2.models

data class ProductResponse(
    val success: Boolean,
    val products: List<Product>,
    val error: String?
)
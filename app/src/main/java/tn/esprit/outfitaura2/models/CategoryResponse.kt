package tn.esprit.outfitaura2.models

data class CategoryResponse(
    val success: Boolean,
    val categories: List<MarketplaceCategory>,
    val error: String? = null
)

data class MarketplaceCategory(
    val id: Int,
    val name: String,
    val image: String? = null, // Added to match Product's Category
    val slug: String? = null
)
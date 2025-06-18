package tn.esprit.outfitaura2.models

data class Product(
    val id: Int,
    val title: String,
    val slug: String,
    val price: Int,
    val description: String,
    val category: Category,
    val images: List<String>
)

data class Category(
    val id: Int,
    val name: String,
    val image: String,
    val slug: String
)
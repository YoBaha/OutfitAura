package tn.esprit.outfitaura2.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import tn.esprit.outfitaura2.models.CategoryResponse
import tn.esprit.outfitaura2.models.ProductResponse

interface MarketplaceService {
    @GET("api/marketplace/products")
    fun getProducts(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("categorySlug") categorySlug: String? = null
    ): Call<ProductResponse>

    @GET("api/marketplace/categories")
    fun getCategories(): Call<CategoryResponse>
}
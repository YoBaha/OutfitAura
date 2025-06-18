package tn.esprit.outfitaura2.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import tn.esprit.outfitaura2.models.DeleteResponse
import tn.esprit.outfitaura2.models.UploadResponse
import tn.esprit.outfitaura2.models.UserImagesResponse

interface ImageService {
    @Multipart
    @POST("api/upload-image")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("prediction") prediction: RequestBody
    ): Call<UploadResponse>

    @GET("api/user/images")
    fun getUserImages(): Call<UserImagesResponse>

    @DELETE("api/images/{id}")
    fun deleteImage(@Path("id") imageId: String): Call<DeleteResponse>
}
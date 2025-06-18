package tn.esprit.outfitaura2.network

import android.content.Context
import android.content.Intent
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tn.esprit.outfitaura2.LoginActivity
import tn.esprit.outfitaura2.MyApplication

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:4000/"
    private var imageService: ImageService? = null
    private var marketplaceService: MarketplaceService? = null
    private var authService: AuthService? = null

    fun init() {
        Log.d("ApiClient", "Initializing ApiClient with BASE_URL: $BASE_URL")
    }

    private fun getToken(context: Context): String? {
        return context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            .getString("jwt_token", null)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor())
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getImageService(context: Context): ImageService {
        if (imageService == null) {
            imageService = retrofit.create(ImageService::class.java)
        }
        return imageService!!
    }

    fun getMarketplaceService(context: Context): MarketplaceService {
        if (marketplaceService == null) {
            marketplaceService = retrofit.create(MarketplaceService::class.java)
        }
        return marketplaceService!!
    }

    fun getAuthService(context: Context): AuthService {
        if (authService == null) {
            authService = retrofit.create(AuthService::class.java)
        }
        return authService!!
    }

    fun <T> tagCall(call: Call<T>, context: Context, onUnauthorized: OnUnauthorizedCallback? = null): Call<T> {
        val request = call.request().newBuilder()
            .tag(Context::class.java, context)
            .apply { if (onUnauthorized != null) tag(OnUnauthorizedCallback::class.java, onUnauthorized) }
            .build()
        Log.w("ApiClient", "Tags applied, but Retrofit Call request cannot be replaced post-creation")
        return call.clone()
    }

    class TokenInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val context = chain.request().tag(Context::class.java) ?: MyApplication.instance.applicationContext
            val onUnauthorized = chain.request().tag(OnUnauthorizedCallback::class.java)
            val token = getToken(context)

            Log.d("ApiClient", "Token added to request: ${token?.take(10)}...")

            val newRequest = chain.request().newBuilder().apply {
                if (token != null) {
                    addHeader("Authorization", "Bearer $token")
                }
            }.build()

            val response = chain.proceed(newRequest)

            if (response.code == 401 && context != null && onUnauthorized != null) {
                Log.e("ApiClient", "Unauthorized (401). Triggering OnUnauthorizedCallback.")
                onUnauthorized.invoke(context)
            }

            return response
        }
    }
}

fun interface OnUnauthorizedCallback {
    fun invoke(context: Context)
}
package tn.esprit.outfitaura2.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tn.esprit.outfitaura2.viewmodels.WeatherService

object ApiClient {
    private const val BASE_URL_AUTH = "http://10.0.2.2:3000/"
    private const val BASE_URL_WEATHER = "https://api.weatherapi.com/v1/"

    private val retrofitAuth = Retrofit.Builder()
        .baseUrl(BASE_URL_AUTH)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitWeather = Retrofit.Builder()
        .baseUrl(BASE_URL_WEATHER)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Services
    val authService: AuthService = retrofitAuth.create(AuthService::class.java)
    val weatherService: WeatherService = retrofitWeather.create(WeatherService::class.java)
}

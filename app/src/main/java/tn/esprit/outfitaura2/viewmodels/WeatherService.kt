package tn.esprit.outfitaura2.viewmodels


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// WeatherService Interface
interface WeatherService {

    @GET("weather")
    fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric" // Default to Celsius
    ): Call<WeatherResponse>

    companion object {
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        fun create(): WeatherService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(WeatherService::class.java)
        }
    }
}

// WeatherResponse Data Class
data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val name: String
) {
    data class Main(
        val temp: Float,
        val pressure: Float,
        val humidity: Int
    )

    data class Weather(
        val description: String,
        val icon: String
    )
}

// WeatherApiClient Class
class WeatherApiClient {

    private val weatherService = WeatherService.create()

    fun getWeather(city: String, apiKey: String, onSuccess: (WeatherResponse) -> Unit, onError: (String) -> Unit) {
        val call = weatherService.getWeather(city, apiKey)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    if (weatherResponse != null) {
                        onSuccess(weatherResponse)
                    } else {
                        onError("No weather data found.")
                    }
                } else {
                    onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                onError("Network Error: ${t.message}")
            }
        })
    }
}

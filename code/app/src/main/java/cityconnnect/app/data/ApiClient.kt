package cityconnnect.app.data

import cityconnect.app.data.MyAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://appcityconnect.000webhostapp.com/student/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: MyAPI by lazy {
        retrofit.create(MyAPI::class.java)
    }
}

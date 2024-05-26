package cityconnnect.app.data


import cityconnect.app.data.MyAPI
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {
    private const val BASE_URL = "https://cityconnectapp.000webhostapp.com/student/"


    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }


    val apiService: MyAPI by lazy {
        retrofit.create(MyAPI::class.java)
    }
}

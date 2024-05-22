package cityconnect.app.data


import cityconnnect.app.data.Complain
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface MyAPI {



    @GET("getComplains.php")
    fun getComplains(): Call<ArrayList<Complain?>?>?


}
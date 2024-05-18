package cityconnect.app.data

import cityconnnect.app.Parkings
import cityconnnect.app.data.User
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface MyAPI {
    @FormUrlEncoded
    @POST("insert.php")
    fun insertData(
        @Field("id") id: Int,
        @Field("username") username: String?,
        @Field("email") email: String?,
        @Field("password") password: String?,
        @Field("name") name: String?
    ): Call<User>

    @GET("select.php")
    fun fetchData(): Call<ArrayList<User?>?>?

    @GET("getParkings.php")
    fun getParkings(): Call<ArrayList<Parkings?>?>?
}
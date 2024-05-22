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

    @FormUrlEncoded
    @POST("insertComplain.php")
    fun insertComplain(
        @Field("complainId") complainId: Int?,
        @Field("title") title: String?,
        @Field("description") description: String?,
        @Field("suggestions") suggestions: String?,
        @Field("photo") photo: Int?,
        @Field("totalRating") totalRating: Float?,
        @Field("date") date: String?,
        @Field("location") location: String?

    ): Call<Complain>

}
package cityconnect.app.data

import cityconnnect.app.data.User
import cityconnnect.app.Report
import cityconnnect.app.Review
import cityconnnect.app.data.Complain
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

    @GET("getComplains.php")
    fun getComplains(): Call<ArrayList<Complain?>?>?

    @GET("getReview.php")
    fun getReview(): Call<ArrayList<Review?>?>?

    @GET("getReport.php")
    fun getReport(): Call<ArrayList<Report?>?>?

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
        @Field("location") location: String?,
        @Field("userId") userId: Int?,
        @Field("totalReviews") totalReviews: Int?

        ): Call<Complain>

    @FormUrlEncoded
    @POST("insertReview.php")
    fun insertReview(
        @Field("userId") userId: Int?,
        @Field("star") star: Int?,
        @Field("complainId") complainId: Int?


    ): Call<Review>

    @FormUrlEncoded
    @POST("insertReport.php")
    fun insertReport(
        @Field("complainId") complainId: Int?,
        @Field("userId") userId: Int?,
        @Field("text") text: String?


    ): Call<Report>
}
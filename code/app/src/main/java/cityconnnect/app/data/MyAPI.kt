package cityconnect.app.data

import cityconnect.app.UserBusTicket
import cityconnnect.app.BusLine
import cityconnnect.app.BusStops
import cityconnnect.app.Parkings
import cityconnnect.app.BusTicket
import cityconnnect.app.data.User
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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
    @GET("getBusStops.php")
    fun getBusStops(): Call<ArrayList<BusStops?>?>?
    @GET("getBusLines.php")
    fun getBusLines(): Call<ArrayList<BusLine?>?>?
    @GET("singleUse_busticket.php")
    fun getSingleUseBusTickets(): Call<ArrayList<BusTicket?>?>?

    @GET("week_busticket.php")
    fun getWeeklyBusTickets(): Call<ArrayList<BusTicket?>?>?

    @GET("monthly_busticket.php")
    fun getMonthlyBusTickets(): Call<ArrayList<BusTicket?>?>?


    @GET("getUserBusTickets.php")
    fun getUserBusTicket(
        @Query("route") route: String,
        @Query("user_id") userId: String,
        @Query("user_cat") userCat: String
    ): Call<List<UserBusTicket>>


}
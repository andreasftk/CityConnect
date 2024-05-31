package cityconnnect.app.data

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
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

    @GET("getPendingBills.php")
    suspend fun getPendingBills(@Query("citizenId") citizenId: Int): List<PendingBill>

    @GET("getPaidBills.php")
    suspend fun getPaidBills(@Query("citizenId") citizenId: Int): List<PaidBill>

    @POST("payBills.php")
    suspend fun payBills(
        @Body request: RequestBody
    ): ResponseBody
}
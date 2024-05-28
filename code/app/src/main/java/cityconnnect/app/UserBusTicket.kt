package cityconnect.app


import android.content.Context
import android.util.Log
import cityconnnect.app.data.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class UserBusTicket(
    val type: String,
    val number: String
) {
    companion object {
        fun getUserBusTickets(context: Context?, route: String, userId: String, userCat: String, callback: (ArrayList<UserBusTicket>) -> Unit) {
            val api = ApiClient.apiService
            val call = api.getUserBusTicket(route, userId, userCat)
            Log.d("UserBusTicket", "Call: $call")
            Log.d("UserBusTicket", "Route: $route")
            Log.d("UserBusTicket", "UserId: $userId")
            Log.d("UserBusTicket", "UserCat: $userCat")

            call.enqueue(object : Callback<List<UserBusTicket>> {
                override fun onResponse(
                    call: Call<List<UserBusTicket>>,
                    response: Response<List<UserBusTicket>>
                ) {
                    if (response.isSuccessful) {
                        val ticketList = response.body()?.let { ArrayList(it) } ?: ArrayList()
                        Log.d("UserBusTicket", "Response: $ticketList")
                        callback(ticketList)
                    } else {
                        // Handle the error appropriately
                        Log.e("UserBusTicket", "Error: ${response.code()}")
                        callback(ArrayList()) // Return an empty list in case of an error
                    }
                }

                override fun onFailure(call: Call<List<UserBusTicket>>, t: Throwable) {
                    Log.e("UserBusTicket", "Error: ${t.message}")
                    // Handle failure appropriately
                    callback(ArrayList()) // Return an empty list in case of failure
                }
            })
        }
    }
}

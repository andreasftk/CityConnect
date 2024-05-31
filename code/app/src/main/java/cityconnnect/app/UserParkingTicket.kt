package cityconnect.app

import android.content.Context
import android.util.Log
import cityconnnect.app.data.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class UserParkingTicket(
    val user_id: Int,
    val parking_ticket_id: Int,
    val number: String,
    val ticket_type: String,
    val region: String

) {
    companion object {
        fun getUserParkingTickets(context: Context?, userId: String, callback: (ArrayList<UserParkingTicket>) -> Unit) {
            val api = ApiClient.apiService
            val call = api.getUserParkingTickets(userId)
            Log.d("UserParkingTicket", "Call: $call")
            Log.d("UserParkingTicket", "UserId: $userId")

            call.enqueue(object : Callback<List<UserParkingTicket>> {
                override fun onResponse(
                    call: Call<List<UserParkingTicket>>,
                    response: Response<List<UserParkingTicket>>
                ) {
                    if (response.isSuccessful) {
                        val ticketList = response.body()?.let { ArrayList(it) } ?: ArrayList()
                        Log.d("UserParkingTicket", "Response: $ticketList")
                        callback(ticketList)
                    } else {
                        // Handle the error appropriately
                        Log.e("UserParkingTicket", "Error: ${response.code()}")
                        callback(ArrayList()) // Return an empty list in case of an error
                    }
                }

                override fun onFailure(call: Call<List<UserParkingTicket>>, t: Throwable) {
                    Log.e("UserParkingTicket", "Error: ${t.message}")
                    // Handle failure appropriately
                    callback(ArrayList()) // Return an empty list in case of failure
                }
            })
        }
    }
}

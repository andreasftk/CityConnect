package cityconnect.app

import android.content.Context
import android.util.Log
import cityconnnect.app.data.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class ParkingTicket(
    val ticket_type: String,
    val duration: String,
    val price: Float,
    val id: Int,
    val region: String
) {
    companion object {
        fun getParkingTickets(context: Context?, callback: (ArrayList<ParkingTicket>) -> Unit) {
            val api = ApiClient.apiService
            val call = api.getParkingTickets()

            call?.enqueue(object : Callback<ArrayList<ParkingTicket?>?> {
                override fun onResponse(
                    call: Call<ArrayList<ParkingTicket?>?>,
                    response: Response<ArrayList<ParkingTicket?>?>
                ) {
                    if (response.isSuccessful) {
                        val ticketList = response.body()?.filterNotNull()?.let { ArrayList(it) } ?: ArrayList()
                        Log.d("ParkingTickets", ticketList.toString())
                        callback(ticketList)
                    } else {
                        // Handle the error appropriately
                        callback(ArrayList()) // Return an empty list in case of an error
                    }
                }

                override fun onFailure(call: Call<ArrayList<ParkingTicket?>?>, t: Throwable) {
                    // Handle failure appropriately
                    callback(ArrayList()) // Return an empty list in case of failure
                }
            })
        }

    }
}

package cityconnnect.app

import android.content.Context
import cityconnnect.app.data.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class BusTicket(
    val route: Int,
    val category: String,
    val cost: Float,
    val id: Int
) {
    companion object {
        fun getSingleUseBusTickets(context: Context?, callback: (ArrayList<BusTicket>) -> Unit) {
            val api = ApiClient.apiService
            val call = api.getSingleUseBusTickets()

            call?.enqueue(object : Callback<ArrayList<BusTicket?>?> {
                override fun onResponse(
                    call: Call<ArrayList<BusTicket?>?>,
                    response: Response<ArrayList<BusTicket?>?>
                ) {
                    if (response.isSuccessful) {
                        val ticketList = response.body()?.filterNotNull()?.let { ArrayList(it) } ?: ArrayList()
                        callback(ticketList)
                    } else {
                        // Handle the error appropriately
                        callback(ArrayList()) // Return an empty list in case of an error
                    }
                }

                override fun onFailure(call: Call<ArrayList<BusTicket?>?>, t: Throwable) {
                    // Handle failure appropriately
                    callback(ArrayList()) // Return an empty list in case of failure
                }
            })
        }
        fun getWeeklyBusTickets(context: Context?, callback: (ArrayList<BusTicket>) -> Unit) {
            val api = ApiClient.apiService
            val call = api.getWeeklyBusTickets()

            call?.enqueue(object : Callback<ArrayList<BusTicket?>?> {
                override fun onResponse(
                    call: Call<ArrayList<BusTicket?>?>,
                    response: Response<ArrayList<BusTicket?>?>
                ) {
                    if (response.isSuccessful) {
                        val ticketList = response.body()?.filterNotNull()?.let { ArrayList(it) } ?: ArrayList()
                        callback(ticketList)
                    } else {
                        // Handle the error appropriately
                        callback(ArrayList()) // Return an empty list in case of an error
                    }
                }

                override fun onFailure(call: Call<ArrayList<BusTicket?>?>, t: Throwable) {
                    // Handle failure appropriately
                    callback(ArrayList()) // Return an empty list in case of failure
                }
            })
        }


        fun getMonthlyBusTickets(context: Context?, callback: (ArrayList<BusTicket>) -> Unit) {
            val api = ApiClient.apiService
            val call = api.getMonthlyBusTickets()

            call?.enqueue(object : Callback<ArrayList<BusTicket?>?> {
                override fun onResponse(
                    call: Call<ArrayList<BusTicket?>?>,
                    response: Response<ArrayList<BusTicket?>?>
                ) {
                    if (response.isSuccessful) {
                        val ticketList = response.body()?.filterNotNull()?.let { ArrayList(it) } ?: ArrayList()
                        callback(ticketList)
                    } else {
                        // Handle the error appropriately
                        callback(ArrayList()) // Return an empty list in case of an error
                    }
                }

                override fun onFailure(call: Call<ArrayList<BusTicket?>?>, t: Throwable) {
                    // Handle failure appropriately
                    callback(ArrayList()) // Return an empty list in case of failure
                }
            })
        }
    }
}

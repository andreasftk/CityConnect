package cityconnnect.app

import android.content.Context
import cityconnnect.app.data.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class BusLine(
    val id: Int,
    val route_number: Int,
) {
    companion object {
        fun getBusLines(context: Context?, callback: (ArrayList<BusLine>) -> Unit) {
            val api = ApiClient.apiService
            val call = api.getBusLines()

            call?.enqueue(object : Callback<ArrayList<BusLine?>?> {
                override fun onResponse(
                    call: Call<ArrayList<BusLine?>?>,
                    response: Response<ArrayList<BusLine?>?>
                ) {
                    if (response.isSuccessful) {
                        val busLineList = response.body()?.filterNotNull()?.let { ArrayList(it) } ?: ArrayList()
                        callback(busLineList)
                    } else {
                        // Handle the error appropriately
                        callback(ArrayList()) // Return an empty list in case of an error
                    }
                }

                override fun onFailure(call: Call<ArrayList<BusLine?>?>, t: Throwable) {
                    // Handle failure appropriately
                    callback(ArrayList()) // Return an empty list in case of failure
                }
            })
        }
    }
}
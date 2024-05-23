package cityconnnect.app

import android.content.Context
import android.util.Log
import cityconnnect.app.data.ApiClient
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class BusStops(
    val route: Int,
    val longitude: Double,
    val latitude: Double,
    val busStopId: Int,
    val name: String,
    @SerializedName("startPoint")
    val startPoint: Int,
    @SerializedName("endPoint")
    val endPoint: Int
) {
    fun startPointBS(): Int {
        return startPoint
    }
    fun endPointBS(): Int {
        return endPoint
    }
    companion object {
        fun getBusStops(context: Context?, callback: (ArrayList<BusStops>) -> Unit) {
            val api = ApiClient.apiService
            val call = api.getBusStops()

            call?.enqueue(object : Callback<ArrayList<BusStops?>?> {
                override fun onResponse(
                    call: Call<ArrayList<BusStops?>?>,
                    response: Response<ArrayList<BusStops?>?>
                ) {
                    if (response.isSuccessful) {
                        val busStopList = response.body()?.filterNotNull()?.let { ArrayList(it) } ?: ArrayList()
                        Log.e("END" , busStopList.toString());
                        callback(busStopList)
                    } else {
                        // Handle the error appropriately
                        callback(ArrayList()) // Return an empty list in case of an error
                    }
                }

                override fun onFailure(call: Call<ArrayList<BusStops?>?>, t: Throwable) {
                    // Handle failure appropriately
                    callback(ArrayList()) // Return an empty list in case of failure
                }
            })
        }
    }
}
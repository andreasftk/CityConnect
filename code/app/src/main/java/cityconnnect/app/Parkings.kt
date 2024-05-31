package cityconnnect.app

import android.content.Context
import android.util.Log
import cityconnnect.app.data.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class Parkings(
    val id: Int,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category_id: String,
    val total_spaces: Int,
    val available_spaces: Int
) {
    fun parAddress(): String {
        return address
    }

    fun parLatitude(): Double {
        return latitude
    }

    fun parLongitude(): Double {
        return longitude
    }

    fun parTotalSpaces(): Int {
        return total_spaces
    }

    fun parAvailableSpaces(): Int {
        return available_spaces
    }

    fun joinToString(): String {
        return "$id, $address, $latitude, $longitude, $total_spaces, $available_spaces"
    }
    companion object {
        fun getParkings(context: Context?, callback: (ArrayList<Parkings>) -> Unit) {
            val api = ApiClient.apiService
            val call = api.getParkings()

            call?.enqueue(object : Callback<ArrayList<Parkings?>?> {
                override fun onResponse(
                    call: Call<ArrayList<Parkings?>?>,
                    response: Response<ArrayList<Parkings?>?>
                ) {
                    if (response.isSuccessful) {
                        val parkingList = response.body()?.filterNotNull()?.let { ArrayList(it) } ?: ArrayList()
                        Log.d("Parkings", parkingList.joinToString())
                        callback(parkingList)
                    } else {
                        // Handle the error appropriately
                        callback(ArrayList()) // Return an empty list in case of an error
                    }
                }

                override fun onFailure(call: Call<ArrayList<Parkings?>?>, t: Throwable) {
                    // Handle failure appropriately
                    callback(ArrayList()) // Return an empty list in case of failure
                }
            })
        }
    }
}

package cityconnnect.app

import android.content.Context
import cityconnnect.app.data.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class ParkingCategories(
    val category: String
) {
    companion object {
        fun getParkingCategories(context: Context?, callback: (ArrayList<ParkingCategories>) -> Unit) {
            val api = ApiClient.apiService
            val call = api.getParkingCategories()

            call.enqueue(object : Callback<ArrayList<ParkingCategories>> {
                override fun onResponse(
                    call: Call<ArrayList<ParkingCategories>>,
                    response: Response<ArrayList<ParkingCategories>>
                ) {
                    if (response.isSuccessful) {
                        val categoryList = response.body()?.filterNotNull()?.let { ArrayList(it) } ?: ArrayList()
                        callback(categoryList)
                    } else {
                        // Handle the error appropriately
                        callback(ArrayList()) // Return an empty list in case of an error
                    }
                }

                override fun onFailure(call: Call<ArrayList<ParkingCategories>>, t: Throwable) {
                    // Handle failure appropriately
                    callback(ArrayList()) // Return an empty list in case of failure
                }
            })
        }
    }
}

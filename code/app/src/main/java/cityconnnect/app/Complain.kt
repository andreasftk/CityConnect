package cityconnnect.app.data

import android.content.Context
import cityconnnect.app.data.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

data class Complain(
    val complainId: Int,
    val title: String,
    val description: String,
    val suggestions: String,
    val photo: Int, // Resource ID
    var totalRating: Float
) {
    companion object {
        private const val TAG = "Complain"

        fun getComplains(context: Context?, callback: (ArrayList<Complain>) -> Unit) {
            val api = ApiClient.apiService
            val call = api.getComplains()
            Log.e(TAG, "Starting API call to fetch complains")

            call?.enqueue(object : Callback<ArrayList<Complain?>?> {
                override fun onResponse(
                    call: Call<ArrayList<Complain?>?>,
                    response: Response<ArrayList<Complain?>?>
                ) {
                    Log.e(TAG, "API call onResponse")

                    if (response.isSuccessful) {
                        val complainList = response.body()?.filterNotNull()?.let { ArrayList(it) } ?: ArrayList()
                        Log.e(TAG, "Fetched ${complainList.size} complains")

                        callback(complainList)
                    } else {
                        // Handle the error appropriately
                        Log.e(TAG, "API call failed with code ${response.code()}")

                        callback(ArrayList()) // Return an empty list in case of an error
                    }
                }

                override fun onFailure(call: Call<ArrayList<Complain?>?>, t: Throwable) {
                    Log.e(TAG, "API call onFailure: ${t.message}")

                    // Handle failure appropriately
                    callback(ArrayList()) // Return an empty list in case of failure
                }
            })
        }
    }

}

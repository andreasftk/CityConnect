package cityconnnect.app.data

import android.content.Context
import cityconnnect.app.data.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import java.sql.Date
import java.sql.Types.NULL

data class Complain(
    val complainId: Int,
    val title: String,
    val description: String,
    val suggestions: String,
    val photo: Int, // Resource ID
    var totalRating: Float,
    val date: String,
    val location: String
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
        fun insertComplain(
            complainId: Int?,
            title: String?,
            description: String?,
            suggestions: String?,
            photo: Int?,
            totalRating: Float?,
            date: String?,
            location: String?,
            callback: (Boolean) -> Unit // Callback to handle success/failure
        ) {
            val api = ApiClient.apiService
            val call = api.insertComplain(NULL, title, description, suggestions, photo, totalRating,"null",location)

            call.enqueue(object : Callback<Complain> {
                override fun onResponse(call: Call<Complain>, response: Response<Complain>) {
                    if (response.isSuccessful) {
                        callback(true) // Notify success
                    } else {
                        callback(false) // Notify failure
                    }
                }

                override fun onFailure(call: Call<Complain>, t: Throwable) {
                    Log.e("Complain", "Failed to insert complain", t)
                    callback(false) // Notify failure
                }
            })
        }

    }


}

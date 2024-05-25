package cityconnnect.app

import android.content.Context
import android.util.Log
import cityconnnect.app.data.ApiClient
import cityconnnect.app.data.Complain
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Types

data class Review (
    val userId: Int,
    val star: Int,
    val complainId: Int,

){

    companion object {
        private const val TAG = "Review"

        fun getReview(context: Context?, callback: (ArrayList<Review>) -> Unit) {
            val api = ApiClient.apiService
            val call = api.getReview()
            Log.e(TAG, "Starting API call to fetch complains")

            call?.enqueue(object : Callback<ArrayList<Review?>?> {
                override fun onResponse(
                    call: Call<ArrayList<Review?>?>,
                    response: Response<ArrayList<Review?>?>
                ) {
                    Log.e(TAG, "API call onResponse")

                    if (response.isSuccessful) {
                        val reviewList = response.body()?.filterNotNull()?.let { ArrayList(it) } ?: ArrayList()
                        Log.e(TAG, "Fetched ${reviewList.size} Review")

                        callback(reviewList)
                    } else {
                        // Handle the error appropriately
                        Log.e(TAG, "API call failed with code ${response.code()}")

                        callback(ArrayList()) // Return an empty list in case of an error
                    }
                }

                override fun onFailure(call: Call<ArrayList<Review?>?>, t: Throwable) {
                    Log.e(TAG, "API call onFailure: ${t.message}")

                    // Handle failure appropriately
                    callback(ArrayList()) // Return an empty list in case of failure
                }
            })
        }
        fun insertReview(
             userId: Int?,
             star: Int?,
             complainId: Int?,
            callback: (Boolean) -> Unit // Callback to handle success/failure
        ) {
            val api = ApiClient.apiService
            val call = api.insertReview(userId,star,complainId)

            call.enqueue(object : Callback<Review> {
                override fun onResponse(call: Call<Review>, response: Response<Review>) {
                    if (response.isSuccessful) {
                        callback(true) // Notify success
                    } else {
                        callback(false) // Notify failure
                    }
                }

                override fun onFailure(call: Call<Review>, t: Throwable) {
                    Log.e("Review", "Failed to insert complain", t)
                    callback(false) // Notify failure
                }
            })
        }

    }

}
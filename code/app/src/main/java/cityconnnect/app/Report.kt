package cityconnnect.app

import android.content.Context
import android.util.Log
import cityconnnect.app.data.ApiClient
import cityconnnect.app.data.Complain
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Types

data class Report (
    val complainId: Int,
    val userId: Int,
    val text: String?,

    ){

    companion object {
        private const val TAG = "Report"

        fun getReport(context: Context?, callback: (ArrayList<Report>) -> Unit) {
            val api = ApiClient.apiService
            val call = api.getReport()
            Log.e(TAG, "Starting API call to fetch complains")

            call?.enqueue(object : Callback<ArrayList<Report?>?> {
                override fun onResponse(
                    call: Call<ArrayList<Report?>?>,
                    response: Response<ArrayList<Report?>?>
                ) {
                    Log.e(TAG, "API call onResponse")

                    if (response.isSuccessful) {
                        val reportList = response.body()?.filterNotNull()?.let { ArrayList(it) } ?: ArrayList()
                        Log.e(TAG, "Fetched ${reportList.size} Report")

                        callback(reportList)
                    } else {
                        // Handle the error appropriately
                        Log.e(TAG, "API call failed with code ${response.code()}")

                        callback(ArrayList()) // Return an empty list in case of an error
                    }
                }

                override fun onFailure(call: Call<ArrayList<Report?>?>, t: Throwable) {
                    Log.e(TAG, "API call onFailure: ${t.message}")

                    // Handle failure appropriately
                    callback(ArrayList()) // Return an empty list in case of failure
                }
            })
        }
        fun insertReport(
             complainId: Int?,
             userId: Int?,
             text: String?,
            callback: (Boolean) -> Unit // Callback to handle success/failure
        ) {
            val api = ApiClient.apiService
            val call = api.insertReport(complainId,userId,text)

            call.enqueue(object : Callback<Report> {
                override fun onResponse(call: Call<Report>, response: Response<Report>) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "Report inserted successfully: ${response.body()}")
                        callback(true) // Notify success
                    } else {
                        Log.e(TAG, "Failed to insert report: ${response.errorBody()?.string()}")
                        callback(false) // Notify failure
                    }
                }

                override fun onFailure(call: Call<Report>, t: Throwable) {
                    Log.e(TAG, "Failed to insert report", t)
                    callback(false) // Notify failure
                }
            })
        }

    }

}
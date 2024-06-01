package cityconnnect.app

import android.content.Context
import android.util.Log
import cityconnnect.app.data.ApiClient
import cityconnnect.app.data.Complain

class History(val complain: Complain, val userId: Int) {
    companion object {
        private const val TAG = "History"

        fun getComplains(context: Context?, userId: Int, callback: (List<Complain>) -> Unit) {
            val api = ApiClient.apiService
            val call = api.getComplains()
            Log.d(TAG, "Starting API call to fetch complains")

            call?.enqueue(object : retrofit2.Callback<ArrayList<Complain?>?> {
                override fun onResponse(
                    call: retrofit2.Call<ArrayList<Complain?>?>,
                    response: retrofit2.Response<ArrayList<Complain?>?>
                ) {
                    Log.d(TAG, "API call onResponse")

                    if (response.isSuccessful) {
                        val complainList = response.body()?.filterNotNull()?.filter { it.userId == userId } ?: emptyList()
                        Log.d(TAG, "Fetched ${complainList.size} Complains for user $userId")

                        callback(complainList)
                    } else {
                        Log.e(TAG, "API call failed with code ${response.code()}")
                        callback(emptyList())
                    }
                }

                override fun onFailure(call: retrofit2.Call<ArrayList<Complain?>?>, t: Throwable) {
                    Log.e(TAG, "API call onFailure: ${t.message}")
                    callback(emptyList())
                }
            })
        }
    }
}

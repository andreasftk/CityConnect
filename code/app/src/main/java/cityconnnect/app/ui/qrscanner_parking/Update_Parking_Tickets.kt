
package cityconnnect.app.ui.qrscanner_parking

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class QrDataRequestSingle(val qrData: String, val userId: String)

data class ServerResponseSingle(val status: String, val result: Int)

interface Update_Parking_Tickets {
    @POST("update_single_user_bus_tickets.php") // Replace with your PHP script's path
    fun confirmSingle(@Body qrDataRequest: QrDataRequest): Call<ServerResponse>


    }


package cityconnnect.app.ui.qrscanner_parking

import cityconnnect.app.ui.qrscanner.QrDataRequest
import cityconnnect.app.ui.qrscanner.ServerResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class QrDataRequestSingle(val qrData: String, val userId: String, val duration: String)

data class ServerResponseSingle(val status: String, val result: Int)

interface Update_Parking_Tickets {
    @POST("check_parking_ticket.php") // Replace with your PHP script's path
    fun confirmSingle(@Body qrDataRequest: QrDataRequest): Call<ServerResponse>


    }

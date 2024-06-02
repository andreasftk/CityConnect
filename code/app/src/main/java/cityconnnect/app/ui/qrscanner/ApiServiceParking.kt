// ApiService.kt

package cityconnnect.app.ui.qrscanner

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class QrDataRequestParking(val qrData: String, val userId: String,val duration: String)


interface ApiServiceParking{
    @POST("check_parking_ticket.php") // Replace with your PHP script's path
    fun sendQrCodeData(@Body qrDataRequest: QrDataRequestParking): Call<ServerResponse>
}



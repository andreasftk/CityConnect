// ApiService.kt

package cityconnnect.app.ui.qrscanner

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class QrDataRequest(val qrData: String, val userId: String)

data class ServerResponse(val status: String, val result: Int)

interface ApiService {
    @POST("check_bus_ticket.php") // Replace with your PHP script's path
    fun sendQrCodeData(@Body qrDataRequest: QrDataRequest): Call<ServerResponse>
}

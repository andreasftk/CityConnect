// ApiService.kt

package cityconnnect.app.ui.qrscanner

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class QrDataRequestParking(val qrData: String, val userId: String,val duration: String)

data class ServerResponseParking(val status: String, val result: Int)

interface ApiServiceParking{
    @POST("test_parking.php") // Replace with your PHP script's path
    fun sendQrCodeData(@Body qrDataRequestParking: QrDataRequestParking): Call<ServerResponse>
}



package cityconnnect.app.ui.qrscanner_parking.zxinglibrary

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cityconnnect.app.R
import cityconnnect.app.databinding.QrCodeMainBinding
import cityconnnect.app.ui.qrscanner.ApiService
import cityconnnect.app.ui.qrscanner.QrDataRequest
import cityconnnect.app.ui.qrscanner.ServerResponse
import cityconnnect.app.ui.qrscanner.Update_Single_Bus_Ticket
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class QrCodeMain : AppCompatActivity() {

    private lateinit var binding: QrCodeMainBinding
    private var qrScanIntegrator: IntentIntegrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = QrCodeMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setOnClickListener()
        setupScanner()
    }

    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator(this)
    }

    private fun setOnClickListener() {
        binding.btnScan.setOnClickListener { performAction() }

        binding.showQRScanner.setOnClickListener {
            // Add code to show QR Scanner Code in Fragment.
        }
    }

    private fun performAction() {
        qrScanIntegrator?.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, getString(R.string.result_not_found), Toast.LENGTH_LONG).show()
            } else {
                // QR Code scan result obtained here
                val qrContent = result.contents
                val userId = "21" // Replace with actual user ID

                sendDataToServer(qrContent, userId) // Pass the result to sendDataToServer method
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun sendDataToServer(scanData: String, userId: String) {


        val retrofit = Retrofit.Builder()
            .baseUrl("https://cityconnectapp.000webhostapp.com/student/") // Replace with your server's base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val qrDataRequest = QrDataRequest(qrData = scanData, userId = userId)

        val call = apiService.sendQrCodeData(qrDataRequest)
        call.enqueue(object : Callback<ServerResponse> {
            override fun onResponse(
                call: Call<ServerResponse>,
                response: Response<ServerResponse>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()?.result
                    when (result) {
                        1 -> no_space_available()
                        2 -> no_ticket_available()
                        3 -> enter_confirm()
                        4 -> out_confirm()
                        5 -> out_and_fined()

                    }
                } else {
                    Toast.makeText(this@QrCodeMain, "Failed to send data", Toast.LENGTH_LONG).show()
                }
            }


            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                Toast.makeText(this@QrCodeMain, "Error: " + t.message, Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun enter_confirm() {

    }

    private fun no_ticket_available() {


    }

    private fun no_space_available(){


    }

    private fun out_confirm(){


    }

    private fun out_and_fined(){


    }

}
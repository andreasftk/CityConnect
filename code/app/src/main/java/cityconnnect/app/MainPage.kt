package cityconnnect.app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cityconnnect.app.databinding.ActivityMainPageBinding
import cityconnnect.app.ui.MainBus
import androidx.activity.enableEdgeToEdge
import cityconnnect.app.ui.BuyBusTickets
import cityconnnect.app.ui.MainParking
import cityconnnect.app.ui.bills.BillSelectionActivity
import cityconnnect.app.ui.qrscanner.ApiService
import cityconnnect.app.ui.qrscanner.ApiServiceParking
import cityconnnect.app.ui.qrscanner.QrDataRequest
import cityconnnect.app.ui.qrscanner.QrDataRequestParking
import cityconnnect.app.ui.qrscanner.ServerResponse
import cityconnnect.app.ui.qrscanner.Update_Single_Bus_Ticket
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainPage : AppCompatActivity() {

    private lateinit var binding: ActivityMainPageBinding
    private var userId: Int = -1 // Class-level variable to store user ID
    private var qrScanIntegrator: IntentIntegrator? = null
    private var scanb: ImageButton? = null
    private var scanp: ImageButton? = null
    var bus_btn: Boolean = false
    var parking_btn: Boolean = false
    private var selectedParkingOption: String? = null // Variable to store selected parking option


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Retrieve the Bundle from the Intent
        val bundle = intent.extras

        // Extract the data from the Bundle
        if (bundle != null) {
            userId = bundle.getInt("id")
        }

        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bus = findViewById<Button>(R.id.bus_btn)
        val parking = findViewById<Button>(R.id.parking_btn)
        val feed = findViewById<Button>(R.id.complain_btn)
        val bills = findViewById<Button>(R.id.button10)
        scanb = findViewById<ImageButton>(R.id.scan_b)
        scanp = findViewById<ImageButton>(R.id.scan_p)


        // Set click listeners for the buttons
        bus.setOnClickListener {
            startNextActivity(MainBus::class.java)
        }
        parking.setOnClickListener {
            startNextActivity(MainParking::class.java)
        }
        feed.setOnClickListener {
            startNextActivity(ComplainMain::class.java)
        }
        bills.setOnClickListener {
            startNextActivity(BillSelectionActivity::class.java)
        }

        setOnClickListener()
        setupScanner()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Helper function to start next activity with user ID
    private fun startNextActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        val bundle = Bundle()
        bundle.putInt("id", userId)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator(this)
    }

    private fun setOnClickListener() {
        scanb?.setOnClickListener {
            bus_btn = true
            performAction()
        }

        scanp?.setOnClickListener {
            parking_btn = true
            showParkingOptions()
        }
    }

    private fun showParkingOptions() {
        val options = arrayOf("1 hour", "3 hours","5 hours","Daily", "Weekly")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select an option")
        builder.setItems(options) { dialog, which ->
            // Handle option selection
            selectedParkingOption = options[which]
            parking_btn = true
            performAction()
        }
        builder.show()
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
                val duration = selectedParkingOption.toString()

                if(bus_btn) {
                    sendDataToServer(qrContent, userId.toString())
                }
                else if(parking_btn){
                    sendDataToServerParking(qrContent, userId.toString(), duration )
                }
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
        val qrDataRequest = QrDataRequest(
            qrData = scanData,
            userId = userId)

        val call = apiService.sendQrCodeData(qrDataRequest)
        call.enqueue(object : Callback<ServerResponse> {
            override fun onResponse(call: Call<ServerResponse>, response: Response<ServerResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()?.result
                    when (result) {
                        1 -> confirmMonthly()
                        2 -> confirmWeekly()
                        3 -> confirmSingle(scanData,userId)
                        else -> buyTicket()
                    }
                } else {
                    Toast.makeText(this@MainPage, "Failed to send data", Toast.LENGTH_LONG).show()
                }
            }


            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                Toast.makeText(this@MainPage, "Error: " + t.message, Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun confirmMonthly() {

    }

    private fun confirmWeekly() {
        // Call your confirm_weekly function here
    }

    private fun confirmSingle(scanData: String, userId: String) {

        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://cityconnectapp.000webhostapp.com/student/") // Replace with your server's base URL
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val updateSingleBusTicket = retrofit.create(Update_Single_Bus_Ticket::class.java)
        val qrDataRequest = QrDataRequest(
            qrData = scanData,
            userId = userId

        )

        val call = updateSingleBusTicket.confirmSingle(qrDataRequest)
        call.enqueue(object : Callback<ServerResponse> {
            override fun onResponse(call: Call<ServerResponse>, response: Response<ServerResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()?.result

                    when (result) {
                        0 -> Toast.makeText(this@MainPage, "Buy new tickets to continue travelling with us", Toast.LENGTH_LONG).show()
                        else -> Toast.makeText(this@MainPage, "Single ticket is valid $result", Toast.LENGTH_LONG).show()
                    }

                }   else {
                    Toast.makeText(this@MainPage, "Failed to send data", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                Toast.makeText(this@MainPage, "Error: " + t.message, Toast.LENGTH_LONG).show()
                setContentView(R.layout.activity_qrscanner)
                val res = findViewById<TextView>(R.id.result)
                res.text = "Error:  $t.message"
            }
        })
    }


    private fun showBuyTicketsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Not enough tickets")
        builder.setMessage("You don't have enough tickets. Do you want to buy more tickets?")
        builder.setPositiveButton("Yes") { dialog, which ->
            val intent = Intent(this, BuyBusTickets::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun buyTicket() {
        showBuyTicketsDialog()
    }
    private fun sendDataToServerParking(scanData: String, userId: String,selectedParkingOption: String) {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://cityconnectapp.000webhostapp.com/student/") // Replace with your server's base URL
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val apiServiceParking = retrofit.create(ApiServiceParking::class.java)
        val qrDataRequestParking = QrDataRequestParking(qrData = scanData, userId = userId, duration = selectedParkingOption)

        val call = apiServiceParking.sendQrCodeData(qrDataRequestParking)
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
                    Toast.makeText(this@MainPage, "Failed to send data", Toast.LENGTH_LONG).show()
                }
            }


            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                Toast.makeText(this@MainPage, "Error: " + t.message, Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun enter_confirm() {

    }

    private fun no_ticket_available() {
        Toast.makeText(this@MainPage, "No ticket available", Toast.LENGTH_LONG).show()

    }

    private fun no_space_available(){
        Toast.makeText(this@MainPage, "No space available", Toast.LENGTH_LONG).show()


    }

    private fun out_confirm(){
        Toast.makeText(this@MainPage, "Out confirmed", Toast.LENGTH_LONG).show()


    }

    private fun out_and_fined(){
        Toast.makeText(this@MainPage, "Out and fined", Toast.LENGTH_LONG).show()


    }
}

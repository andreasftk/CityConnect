package cityconnnect.app.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cityconnnect.app.*
import cityconnnect.app.ui.qrscanner.ApiService
import cityconnnect.app.ui.qrscanner.QrDataRequest
import cityconnnect.app.ui.qrscanner.ServerResponse
import cityconnnect.app.ui.qrscanner.Update_Single_Bus_Ticket
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainBus : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var rvLines: RecyclerView
    private var selectedMarker: Marker? = null
    private var userId: Int = -1 // Class-level variable to store user ID
    private lateinit var lineAdapter: BusLineAdapter
    private val markers = mutableListOf<Marker>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var allBusStops: List<BusStops>

    private var qrScanIntegrator: IntentIntegrator? = null
    private var scanb: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the Bundle from the Intent
        val bundle = intent.extras

        // Extract the data from the Bundle
        if (bundle != null) {
            userId = bundle.getInt("id")
        }

        Configuration.getInstance().load(applicationContext, androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContentView(R.layout.activity_main_bus)

        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(20.0)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        val centerPoint = GeoPoint(38.246639, 21.734573)
        mapView.controller.setCenter(centerPoint)

        rvLines = findViewById(R.id.rvLines)
        val layoutManager = LinearLayoutManager(this@MainBus,LinearLayoutManager.HORIZONTAL,false)
        rvLines.layoutManager = layoutManager
        rvLines.layoutManager = LinearLayoutManager(this)
        lineAdapter = BusLineAdapter(mutableListOf())
        rvLines.adapter = lineAdapter

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()

        val zoomInButton: ImageButton = findViewById(R.id.zoomInButton)
        val zoomOutButton: ImageButton = findViewById(R.id.zoomOutButton)

        zoomInButton.setOnClickListener {
            mapView.controller.zoomIn()
        }

        zoomOutButton.setOnClickListener {
            mapView.controller.zoomOut()
        }

        fetchAndAddBusStops()
        fetchBusLines()

        scanb = findViewById<ImageButton>(R.id.scan_b)

        setOnClickListener()
        setupScanner()

        val btnbuy = findViewById<ImageButton>(R.id.btBuy)
        btnbuy.setOnClickListener {
            val intent = Intent(this, BuyBusTickets::class.java)
            val bundle = Bundle()
            bundle.putInt("id", userId)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        val btnhistory = findViewById<Button>(R.id.btHistory)
        btnbuy.setOnClickListener {
            val intent = Intent(this, BuyBusTickets::class.java)
            val bundle = Bundle()
            bundle.putInt("id", userId)
            intent.putExtras(bundle)
            startActivity(intent)
        }

    }

    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator(this)
    }

    private fun setOnClickListener() {
        scanb?.setOnClickListener {
            performAction()
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

                    sendDataToServer(qrContent, userId.toString())
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
                    Toast.makeText(this@MainBus, "Failed to send data", Toast.LENGTH_LONG).show()
                }
            }


            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                Toast.makeText(this@MainBus, "Error: " + t.message, Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun confirmMonthly() {
        Toast.makeText(this@MainBus, "Monthly ticket is valid", Toast.LENGTH_LONG).show()
    }

    private fun confirmWeekly() {
        // Call your confirm_weekly function here
        Toast.makeText(this@MainBus, "Weekly ticket is valid", Toast.LENGTH_LONG).show()
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
                        0 -> Toast.makeText(this@MainBus, "Buy new tickets to continue travelling with us", Toast.LENGTH_LONG).show()
                        else -> Toast.makeText(this@MainBus, "Single ticket is valid $result", Toast.LENGTH_LONG).show()
                    }

                }   else {
                    Toast.makeText(this@MainBus, "Failed to send data", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                Toast.makeText(this@MainBus, "Error: " + t.message, Toast.LENGTH_LONG).show()
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
            val bundle = Bundle()
            bundle.putInt("id", userId)
            intent.putExtras(bundle)
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

    private fun fetchAndAddBusStops() {
        BusStops.getBusStops(this) { busStops ->
            allBusStops = busStops
        }
    }

    private fun fetchBusLines() {
        BusLine.getBusLines(this) { busLines ->
            setupRecyclerView(busLines)
        }
    }

    private fun setupRecyclerView(busLines: ArrayList<BusLine>) {
        lineAdapter = BusLineAdapter(busLines.toMutableList())
        rvLines.adapter = lineAdapter

        lineAdapter.setItemClickListener(object : BusLineAdapter.ItemClickListener {
            override fun onItemClick(line: BusLine) {
                showBusStopsForLine(line.id)
            }
        })
    }

    private fun showBusStopsForLine(lineId: Int) {
        mapView.overlays.clear() // Clear existing markers

        val filteredBusStops = allBusStops.filter { it.route == lineId }
        for (busStop in filteredBusStops) {
            addMarker(busStop)
        }
        getCurrentLocation()
        mapView.invalidate() // Refresh the map view
    }

    private fun addMarker(busStop: BusStops) {
        val marker = Marker(mapView).apply {
            position = GeoPoint(busStop.latitude, busStop.longitude)
            title = busStop.name
            relatedObject = busStop // Storing the bus stop object in the marker for later use
        }


        if (busStop.startPointBS() == 1 ) {
            marker.icon = ContextCompat.getDrawable(this, R.drawable.custom_marker_shape_start)
        } else if (busStop.endPointBS() == 1) {
            marker.icon = ContextCompat.getDrawable(this, R.drawable.custom_marker_shape_end)
        } else {
            marker.icon = ContextCompat.getDrawable(this, R.drawable.custom_marker_shape_clicked)
        }

        marker.setOnMarkerClickListener { clickedMarker, _ ->
            showMarkerInfo(clickedMarker)
            true
        }
        mapView.overlays.add(marker)
        markers.add(marker) // Add marker to the list of markers
    }


    private fun showMarkerInfo(marker: Marker) {
        selectedMarker = marker
        marker.icon = ContextCompat.getDrawable(this@MainBus, R.drawable.custom_marker_shape_clicked)
        val busStop = marker.relatedObject as? BusStops
        if (busStop != null) {
            marker.apply {
                snippet = "Route: ${busStop.route}\nBus Stop ID: ${busStop.busStopId}"
                title = busStop.name
                infoWindow = BusStopsInfoWindowAdapter(this@MainBus, mapView) {
                    resetSelectedMarker()
                }
                showInfoWindow() // Explicitly show the info window
            }
        } else {
            marker.icon = ContextCompat.getDrawable(this@MainBus, R.drawable.custom_marker_shape)
            marker.infoWindow = null // Remove info window adapter if bus stop info not available
        }
        mapView.invalidate() // Refresh the map view
    }

    private fun resetSelectedMarker() {
        val busStop = selectedMarker!!.relatedObject as? BusStops
        if (busStop!!.startPointBS() == 1 ) {
            selectedMarker?.icon = ContextCompat.getDrawable(this, R.drawable.custom_marker_shape_start)
        } else if (busStop.endPointBS() == 1) {
            selectedMarker?.icon = ContextCompat.getDrawable(this, R.drawable.custom_marker_shape_end)
        } else {
            selectedMarker?.icon =
                ContextCompat.getDrawable(this, R.drawable.custom_marker_shape_clicked)

        }
        selectedMarker = null
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task: Task<android.location.Location> ->
            if (task.isSuccessful && task.result != null) {
                val currentLocation = task.result
                val currentGeoPoint = GeoPoint(currentLocation.latitude, currentLocation.longitude)
                addCurrentLocationMarker(currentGeoPoint)
            }
        }
    }

    private fun addCurrentLocationMarker(currentGeoPoint: GeoPoint) {
        val currentLocationMarker = Marker(mapView).apply {
            position = currentGeoPoint
            icon = ContextCompat.getDrawable(this@MainBus, R.drawable.custom_marker_shape_current_location)
            title = "You are here"
            snippet = "Current location"
            infoWindow = CustomInfoWindowAdapter(mapView)
        }
        mapView.overlays.add(currentLocationMarker)
        mapView.controller.setCenter(currentGeoPoint)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
// MapsActivity.kt
package cityconnnect.app.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cityconnnect.app.R
import cityconnnect.app.CustomInfoWindowAdapter
import cityconnnect.app.Parkings
import cityconnnect.app.ParkingAdapter
import cityconnnect.app.ParkingInfoWindowAdapter
import cityconnnect.app.ui.qrscanner.ApiServiceParking
import cityconnnect.app.ui.qrscanner.QrDataRequestParking
import cityconnnect.app.ui.qrscanner.ServerResponse
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

class MainParking : AppCompatActivity() {

    private lateinit var mapView: MapView

    private lateinit var rvParkings: RecyclerView
    private var selectedMarker: Marker? = null
    private lateinit var parkingAdapter: ParkingAdapter
    private val markers = mutableListOf<Marker>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var userId: Int = -1 // Class-level variable to store user ID

    private var qrScanIntegrator: IntentIntegrator? = null
    private var scanp: ImageButton? = null

    private var selectedParkingOption: String? = null // Variable to store selected parking option


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContentView(R.layout.activity_maps)

        val bundle = intent.extras

        // Extract the data from the Bundle
        if (bundle != null) {
            userId = bundle.getInt("id")
        }

        mapView = findViewById(R.id.mapView)


        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(20.0)
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        val centerPoint = GeoPoint(38.246639, 21.734573)
        mapView.controller.setCenter(centerPoint)

        // Initialize RecyclerView
        rvParkings = findViewById(R.id.rvParkings)
        rvParkings.layoutManager = LinearLayoutManager(this)
        parkingAdapter = ParkingAdapter(mutableListOf())
        rvParkings.adapter = parkingAdapter

        // Fetch and add parking markers
        fetchAndAddParkings()

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
        scanp = findViewById<ImageButton>(R.id.scan_p)



        setOnClickListener()
        setupScanner()
    }

    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator(this)
    }

    private fun setOnClickListener() {

        scanp?.setOnClickListener {

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

                sendDataToServerParking(qrContent, userId.toString(), duration )
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun fetchAndAddParkings() {
        Parkings.getParkings(this) { parkings ->
            for (parking in parkings) {
                addMarker(parking)
            }
            setupRecyclerView(parkings)
        }
    }

    private fun setupRecyclerView(parkings: List<Parkings>) {
        parkingAdapter = ParkingAdapter(parkings.toMutableList())
        rvParkings.adapter = parkingAdapter

        parkingAdapter.setItemClickListener(object : ParkingAdapter.ItemClickListener {
            override fun onItemClick(parking: Parkings) {
                val marker = markers.find { it.title == parking.address }
                marker?.let {
                    mapView.controller.setCenter(it.position)
                    showMarkerInfo(it)
                }
            }
            override fun onGoToPageClick(parking: Parkings) {

                val intent = Intent(this@MainParking, BuyParkingTickets::class.java)
                val bundle = Bundle()
                bundle.putInt("id", userId)
                bundle.putInt("parking_id", parking.id)
                bundle.putString("category_id", parking.category_id)
                intent.putExtras(bundle)
                startActivity(intent)

            }
        })
    }

    private fun addMarker(parking: Parkings) {
        val marker = Marker(mapView).apply {
            position = GeoPoint(parking.latitude, parking.longitude)
            icon = ContextCompat.getDrawable(this@MainParking, R.drawable.custom_marker_shape)
            title = parking.address
            relatedObject = parking // Storing the parking object in the marker for later use
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
        marker.icon = ContextCompat.getDrawable(this@MainParking, R.drawable.custom_marker_shape_clicked)
        val parking = marker.relatedObject as? Parkings
        if (parking != null) {
            marker.apply {
                snippet = "Total Spaces: ${parking.total_spaces}\nAvailable Spaces: ${parking.available_spaces}"
                title = parking.address
                infoWindow = ParkingInfoWindowAdapter(
                    this@MainParking,
                    mapView,
                    parking.id,  // Pass parking id
                    parking.category_id,  // Pass category id
                    userId,  // Pass user id
                    onCloseCallback = { resetSelectedMarker() }
                )
                 // Explicitly show the info window
                showInfoWindow() // Explicitly show the info window
            }
        } else {
            marker.icon = ContextCompat.getDrawable(this@MainParking, R.drawable.custom_marker_shape)
            marker.infoWindow = null // Remove info window adapter if parking info not available
        }
        mapView.invalidate() // Refresh the map view
    }
    private fun resetSelectedMarker() {
        selectedMarker?.icon = ContextCompat.getDrawable(this, R.drawable.custom_marker_shape)
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
            icon = ContextCompat.getDrawable(this@MainParking, R.drawable.custom_marker_shape_current_location)
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
                    Toast.makeText(this@MainParking, "Failed to send data", Toast.LENGTH_LONG).show()
                }
            }


            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                Toast.makeText(this@MainParking, "Error: " + t.message, Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun enter_confirm() {

    }

    private fun no_ticket_available() {
        Toast.makeText(this@MainParking, "No ticket available", Toast.LENGTH_LONG).show()

    }

    private fun no_space_available(){
        Toast.makeText(this@MainParking, "No space available", Toast.LENGTH_LONG).show()


    }

    private fun out_confirm(){
        Toast.makeText(this@MainParking, "Out confirmed", Toast.LENGTH_LONG).show()


    }

    private fun out_and_fined(){
        Toast.makeText(this@MainParking, "Out and fined", Toast.LENGTH_LONG).show()


    }
}

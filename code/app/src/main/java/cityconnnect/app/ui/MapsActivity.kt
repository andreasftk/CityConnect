// MapsActivity.kt
package cityconnnect.app.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cityconnnect.app.R
import android.graphics.Color
import cityconnnect.app.CustomInfoWindowAdapter
import cityconnnect.app.Parkings
import cityconnnect.app.ParkingAdapter
import cityconnnect.app.ParkingInfoWindowAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapAdapter
import org.osmdroid.events.ScrollEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapsActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    private lateinit var rvParkings: RecyclerView
    private var selectedMarker: Marker? = null
    private lateinit var parkingAdapter: ParkingAdapter
    private val markers = mutableListOf<Marker>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContentView(R.layout.activity_maps)

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

                val intent = Intent(this@MapsActivity, SpecificParkingPage::class.java).apply {
                    putExtra(SpecificParkingPage.EXTRA_PARKING_NAME, parking.address)
                }
                startActivity(intent)
            }
        })
    }

    private fun addMarker(parking: Parkings) {
        val marker = Marker(mapView).apply {
            position = GeoPoint(parking.latitude, parking.longitude)
            icon = ContextCompat.getDrawable(this@MapsActivity, R.drawable.custom_marker_shape)
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
        marker.icon = ContextCompat.getDrawable(this@MapsActivity, R.drawable.custom_marker_shape_clicked)
        val parking = marker.relatedObject as? Parkings
        if (parking != null) {
            marker.apply {
                snippet = "Total Spaces: ${parking.total_spaces}\nAvailable Spaces: ${parking.available_spaces}"
                title = parking.address
                infoWindow = ParkingInfoWindowAdapter(this@MapsActivity, mapView) {
                    resetSelectedMarker()
                }
                showInfoWindow() // Explicitly show the info window
            }
        } else {
            marker.icon = ContextCompat.getDrawable(this@MapsActivity, R.drawable.custom_marker_shape)
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
            icon = ContextCompat.getDrawable(this@MapsActivity, R.drawable.custom_marker_shape_current_location)
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

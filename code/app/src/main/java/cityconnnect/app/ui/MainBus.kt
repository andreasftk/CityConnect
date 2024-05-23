package cityconnnect.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cityconnnect.app.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MainBus : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var rvLines: RecyclerView
    private var selectedMarker: Marker? = null
    private lateinit var lineAdapter: BusLineAdapter
    private val markers = mutableListOf<Marker>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var allBusStops: List<BusStops>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        //Log.e("END" , busStop.endPointBS());
        //Log.e("start" , busStop.startPointBS());

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
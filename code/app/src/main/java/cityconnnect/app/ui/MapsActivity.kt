// MapsActivity.kt
package cityconnnect.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cityconnnect.app.R
import cityconnnect.app.Parkings
import cityconnnect.app.ParkingAdapter
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapAdapter
import org.osmdroid.events.ScrollEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapsActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var cardView: androidx.cardview.widget.CardView
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var rvParkings: RecyclerView
    private lateinit var closeButton: ImageButton
    private var selectedMarker: Marker? = null
    private lateinit var parkingAdapter: ParkingAdapter
    private val markers = mutableListOf<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContentView(R.layout.activity_maps)

        val rootLayout: ConstraintLayout = findViewById(R.id.relativeLayout)
        mapView = findViewById(R.id.mapView)

        // Inflate the card view from card_view.xml
        val inflater = LayoutInflater.from(this)
        val cardViewLayout = inflater.inflate(R.layout.card_view, rootLayout, false)
        cardView = cardViewLayout.findViewById(R.id.cardView)
        titleTextView = cardViewLayout.findViewById(R.id.title)
        descriptionTextView = cardViewLayout.findViewById(R.id.description)
        closeButton = cardViewLayout.findViewById(R.id.closeButton)

        // Add the card view to the root layout
        rootLayout.addView(cardView)

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(20.0)
        val centerPoint = GeoPoint(38.246639, 21.734573)
        mapView.controller.setCenter(centerPoint)

        // Initialize RecyclerView
        rvParkings = findViewById(R.id.rvParkings)
        rvParkings.layoutManager = LinearLayoutManager(this)
        parkingAdapter = ParkingAdapter(mutableListOf())
        rvParkings.adapter = parkingAdapter

        // Fetch and add parking markers
        fetchAndAddParkings()

        mapView.addMapListener(object : MapAdapter() {
            override fun onScroll(event: ScrollEvent?): Boolean {
                selectedMarker?.let { marker ->
                    // Calculate the new position of the card view above the marker
                    val markerPosition = mapView.projection.toPixels(marker.position, null)
                    val cardWidth = cardView.width
                    val cardHeight = cardView.height
                    val mapHeight = mapView.height

                    val cardX = markerPosition.x - (cardWidth / 2)
                    val cardY = markerPosition.y - cardHeight - (marker.icon?.intrinsicHeight ?: 0)

                    // Set the position of the card view
                    cardView.x = cardX.toFloat()
                    cardView.y = cardY.toFloat()
                }
                return super.onScroll(event)
            }
        })

        closeButton.setOnClickListener {
            cardView.visibility = View.GONE
            selectedMarker!!.icon = ContextCompat.getDrawable(this@MapsActivity, R.drawable.custom_marker_shape)
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
            titleTextView.text = marker.title
            descriptionTextView.text = "Total Spaces: ${parking.total_spaces}\nAvailable Spaces: ${parking.available_spaces}"

            // Calculate the position of the card view above the marker
            val markerPosition = mapView.projection.toPixels(marker.position, null)
            val cardWidth = cardView.width
            val cardHeight = cardView.height
            val mapHeight = mapView.height

            val cardX = markerPosition.x - (cardWidth / 2)
            val cardY = markerPosition.y - cardHeight - (marker.icon?.intrinsicHeight ?: 0)

            cardView.y = cardY.toFloat()
            cardView.x = cardX.toFloat()

            cardView.visibility = View.VISIBLE
        } else {
            cardView.visibility = View.GONE
            marker.icon = ContextCompat.getDrawable(this@MapsActivity, R.drawable.custom_marker_shape)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}

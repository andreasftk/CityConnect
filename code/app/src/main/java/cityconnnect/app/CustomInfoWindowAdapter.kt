package cityconnnect.app

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow
import cityconnnect.app.R

class CustomInfoWindowAdapter(mapView: MapView) : InfoWindow(R.layout.custom_info_window, mapView) {

    override fun onOpen(item: Any?) {
        val marker = item as? Marker ?: return
        val titleTextView: TextView = mView.findViewById(R.id.title)
        val snippetTextView: TextView = mView.findViewById(R.id.snippet)
        val closeButton: ImageButton = mView.findViewById(R.id.close)


        titleTextView.text = marker.title
        snippetTextView.text = marker.snippet
        closeButton.setOnClickListener {
            close()
        }
    }

    override fun onClose() {
        // Implement any additional behavior when the info window is closed if needed
    }
}

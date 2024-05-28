package cityconnnect.app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import cityconnnect.app.ui.MainBus
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

class BusStopsInfoWindowAdapter(
    private val context: Context,
    private val mapView: MapView,
    private val onCloseCallback: () -> Unit
) : InfoWindow(LayoutInflater.from(context).inflate(R.layout.bus_stop_info_window, null), mapView) {

    override fun onOpen(item: Any?) {
        val marker = item as? Marker ?: return
        val nameTextView: TextView = mView.findViewById(R.id.bus_stop_name)
        val idTextView: TextView = mView.findViewById(R.id.bus_stop_id)
        val closeButton: ImageButton = mView.findViewById(R.id.closeButton)
        val goToPageButton: Button = mView.findViewById(R.id.go_to_page_button)

        nameTextView.text = marker.title
        idTextView.text = marker.snippet

        closeButton.setOnClickListener {
            close()
        }

    }

    override fun onClose() {
        onCloseCallback()
    }
}
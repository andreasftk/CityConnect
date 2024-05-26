package cityconnnect.app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.GridLayout.Spec
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import cityconnnect.app.ui.MainBus
import cityconnnect.app.ui.SpecificParkingPage
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

class ParkingInfoWindowAdapter(
    private val context: Context,
    private val mapView: MapView,
    private val onCloseCallback: () -> Unit
) : InfoWindow(LayoutInflater.from(context).inflate(R.layout.card_view, null), mapView) {

    override fun onOpen(item: Any?) {
        val marker = item as? Marker ?: return
        val titleTextView: TextView = mView.findViewById(R.id.title)
        val descriptionTextView: TextView = mView.findViewById(R.id.description)
        val closeButton: ImageButton = mView.findViewById(R.id.closeButton)
        val goToPageButton: Button = mView.findViewById(R.id.button3)

        titleTextView.text = marker.title
        descriptionTextView.text = marker.snippet

        closeButton.setOnClickListener {
            close()
        }

        goToPageButton.setOnClickListener {
            val parkingName = marker.title
            val intent = Intent(context, SpecificParkingPage::class.java).apply {
                putExtra(SpecificParkingPage.EXTRA_PARKING_NAME, parkingName)
            }
            context.startActivity(intent)
        }
    }

    override fun onClose() {
        onCloseCallback() // Invoke the callback to reset the marker icon
    }
}

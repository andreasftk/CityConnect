// ParkingDetailsActivity.kt
package cityconnnect.app.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cityconnnect.app.R

class SpecificParkingPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.specific_parking_activity)


        val parkingNameTextView: TextView = findViewById(R.id.parkingNameTextView)

    }

    companion object {

    }
}

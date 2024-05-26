package cityconnnect.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cityconnnect.app.databinding.ActivityMainPageBinding
import cityconnnect.app.ui.MainBus
import androidx.activity.enableEdgeToEdge
import cityconnnect.app.ui.MapsActivity

class MainPage : AppCompatActivity() {

    private lateinit var binding: ActivityMainPageBinding
    private var userId: Int = -1 // Class-level variable to store user ID

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

        // Set click listeners for the buttons
        bus.setOnClickListener {
            startNextActivity(MainBus::class.java)
        }
        parking.setOnClickListener {
            startNextActivity(MapsActivity::class.java)
        }
        feed.setOnClickListener {
            startNextActivity(ComplainMain::class.java)
        }

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
}

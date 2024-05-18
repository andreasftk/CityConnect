package cityconnnect.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cityconnnect.app.databinding.ActivityMainPageBinding
import cityconnnect.app.ui.MainBus
import cityconnnect.app.ui.MapsActivity

class MainPage : AppCompatActivity() {

    private lateinit var binding: ActivityMainPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bus = findViewById<Button>(R.id.bus_btn)
        val parking = findViewById<Button>(R.id.parking_btn)

        bus.setOnClickListener {
            val intent = Intent(this, MainBus::class.java)
            startActivity(intent)
        }
        parking.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

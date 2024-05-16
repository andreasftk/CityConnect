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

class MainPage : AppCompatActivity() {

    private lateinit var binding: ActivityMainPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bus = findViewById<Button>(R.id.bus_btn)

        bus.setOnClickListener {
            val intent = Intent(this, MainBus::class.java)
            startActivity(intent)
        }






        setContentView(R.layout.activity_main_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
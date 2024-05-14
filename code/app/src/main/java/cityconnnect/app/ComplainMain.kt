package cityconnnect.app

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ComplainMain : AppCompatActivity() {
    private lateinit var buttonFeed: Button
    private lateinit var buttonHistory: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_complain_main) // Set correct layout resource

        buttonFeed = findViewById(R.id.btFeed)
        buttonHistory = findViewById(R.id.btHistory)

        // Set initial states (buttonFeed enabled, buttonHistory disabled)
        buttonFeed.isEnabled = false
        buttonHistory.isEnabled = true

        updateButtonBackground(buttonHistory)
        updateButtonBackground(buttonFeed)


        buttonFeed.setOnClickListener {
            buttonFeed.isEnabled = false
            buttonHistory.isEnabled = true
            updateButtonBackground(buttonHistory)
            updateButtonBackground(buttonFeed)
        }

        buttonHistory.setOnClickListener {
            buttonHistory.isEnabled = false
            buttonFeed.isEnabled = true
            updateButtonBackground(buttonHistory)
            updateButtonBackground(buttonFeed)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
    }

    private fun updateButtonBackground(button: Button) {
        if (button.isEnabled) {
            button.setTextAppearance(R.style.buttonBlackBackground)
        } else {
            button.setTextAppearance(R.style.buttonWhiteBackground)
        }
    }
}

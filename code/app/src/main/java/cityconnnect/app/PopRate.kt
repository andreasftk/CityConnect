package cityconnnect.app
import android.os.Bundle
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class PopRate : AppCompatActivity() {

    private lateinit var ratingBar: RatingBar
    private lateinit var tvCurRating: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pop_rate)

        // Initialize views
        ratingBar = findViewById(R.id.ivRate)
        tvCurRating = findViewById(R.id.tvCurRating)

        // Set listener on RatingBar to update the TextView
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            // Update the text view with the selected rating
            tvCurRating.text = String.format("%.1f/5", rating)
        }

        // Handle your rate button click here
        val btRate = findViewById<AppCompatButton>(R.id.btRate)
        btRate.setOnClickListener {
            // Implement your rate button logic here
            // For example, you can retrieve the current rating from 'ratingBar.rating'
        }
    }
}

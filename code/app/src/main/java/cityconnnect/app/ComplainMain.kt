package cityconnnect.app

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.Toast
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.compose.ui.graphics.Color
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class ComplainMain : AppCompatActivity(), ComplainAdapter.RateButtonClickListener {
    private lateinit var buttonFeed: Button
    private lateinit var buttonHistory: Button
    private lateinit var buttonPlus: Button
    //private lateinit var buttonRate: Button
    private lateinit var complainAdapter: ComplainAdapter
    private lateinit var rvComplains: RecyclerView
    private lateinit var searchView: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_complain_main) // Set correct layout resource

        rvComplains = findViewById(R.id.rvComplains)
        rvComplains.setHasFixedSize(true) // Optional: Improve performance if size won't change
        complainAdapter = ComplainAdapter(getSampleComplains()) // Pass your list of complains here
        //complainAdapter = ComplainAdapter(mutableListOf())
        rvComplains.adapter = complainAdapter
        rvComplains.layoutManager = LinearLayoutManager(this)


        buttonFeed = findViewById(R.id.btFeed)
        buttonHistory = findViewById(R.id.btHistory)
        buttonPlus = findViewById(R.id.btPlus)
        searchView = findViewById(R.id.searchView)
        setupSearchView()



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
    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // This method is called when the user submits the query
                // Perform search logic here (e.g., filter your data based on the query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // This method is called when the text in the search view changes
                // Perform live filtering as the user types
                complainAdapter.filter.filter(newText)
                return true
            }
        })
    }
    override fun onRateButtonClick(complain: Complain) {
        showRate();
    }

    private fun showRate() {
         val ratingBar: RatingBar = findViewById(R.id.ivRate)
         val tvCurRating: TextView = findViewById(R.id.tvCurRating)
        val btRate = findViewById<AppCompatButton>(R.id.btRate)

        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.activity_complain_rate, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()

        // Set the layout parameters
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        bottomSheetDialog.window?.setLayout(layoutParams.width, layoutParams.height)
        bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        bottomSheetDialog.window?.setWindowAnimations(R.style.DialogAnimation)
        bottomSheetDialog.window?.setGravity(Gravity.BOTTOM)

        // Set listener on RatingBar to update the TextView
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            if (rating > 0) {
                btRate.isEnabled = true
                btRate.setBackgroundResource(R.drawable.rate_button_enabled)

                tvCurRating.visibility = TextView.VISIBLE

                // Calculate the text size based on the rating (adjust multiplier for desired scaling effect)
                val textSizeMultiplier = 10f // Adjust this value to control the scaling effect
                val textSize = rating * textSizeMultiplier

                // Update the text and set the text size
                tvCurRating.text = String.format("%s / 5", rating)
                tvCurRating.textSize = textSize
            } else {
                btRate.isEnabled = false
                btRate.setBackgroundResource(R.drawable.rate_button)
                // Rating is 0, hide the text view
                tvCurRating.visibility = TextView.GONE
            }
        }

        // Handle your rate button click here
        btRate.setOnClickListener {
            // Implement your rate button logic here
            // For example, you can retrieve the current rating from 'ratingBar.rating'
        }

    }

}
    private fun updateButtonBackground(button: Button) {
        if (button.isEnabled) {
            button.setTextAppearance(R.style.buttonBlackBackground)
        } else {
            button.setTextAppearance(R.style.buttonWhiteBackground)
        }
    }
    private fun getSampleComplains(): MutableList<Complain> {
        // Sample data for testing
        return mutableListOf(
            Complain("Title 1", "Description 1","ic_rubbish.png" ,3.5f),
            Complain("Title 2", "Description 2", "ic_rubbish.png",4.0f),
            Complain("Title 3", "Description 3","ic_rubbish.png" ,2.0f)
            // Add more complains as needed
        )
    }


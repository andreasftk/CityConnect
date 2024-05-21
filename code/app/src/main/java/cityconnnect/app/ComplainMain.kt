package cityconnnect.app

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RatingBar
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class ComplainMain : AppCompatActivity(), ComplainAdapter.ImageButtonClickListener  {
    private lateinit var buttonFeed: Button
    private lateinit var buttonHistory: Button
    private lateinit var buttonPlus: ImageButton
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
            searchView.clearFocus()
            buttonFeed.isEnabled = false
            buttonHistory.isEnabled = true
            updateButtonBackground(buttonHistory)
            updateButtonBackground(buttonFeed)
        }

        buttonHistory.setOnClickListener {
            searchView.clearFocus()
            buttonHistory.isEnabled = false
            buttonFeed.isEnabled = true
            updateButtonBackground(buttonHistory)
            updateButtonBackground(buttonFeed)
        }

        buttonPlus.setOnClickListener {
            searchView.clearFocus()
            showNewComplainForm()
        }

        complainAdapter.setImageButtonClickListener(this)

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

    private val mainHandler = Handler(Looper.getMainLooper())
    private var isRateButtonClickable = true

    override fun onImageButtonClick(complain: Complain, buttonType: ComplainAdapter.ButtonType) {
        searchView.clearFocus()
        if (isRateButtonClickable) {
            // Check which button is clicked and show the respective bottom sheet
            when (buttonType) {
                ComplainAdapter.ButtonType.RATE -> showRate()
                ComplainAdapter.ButtonType.COMMENT -> showComment()
                else -> {

                }
            }

            // Disable the button to prevent multiple clicks
            isRateButtonClickable = false
            // Re-enable the button after a delay
            mainHandler.postDelayed({
                isRateButtonClickable = true
            }, 1000) // delay time (in milliseconds)
        }
    }
    private fun showNewComplainForm() {
        searchView.clearFocus()
        val formCompleted = false
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.activity_complain_form, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()


        // Find editText inside the activity_complain_form layout
        val etDate: EditText = view.findViewById(R.id.etDate)
        val etLocation: EditText = view.findViewById(R.id.etLocation)
        val etFormTitle: EditText = view.findViewById(R.id.etFormTitle)
        val etDescription: EditText = view.findViewById(R.id.etDescription)
        val ibCamera: ImageButton = view.findViewById(R.id.ibCamera)
        val ibLocation: ImageButton = view.findViewById(R.id.ibLocation)
        val btSubmit: AppCompatButton = view.findViewById(R.id.btSubmit)


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
        btSubmit.setOnClickListener {
            if (formCompleted) {
                bottomSheetDialog.dismiss()
                Toast.makeText(this,"Complain Submitted",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,"Required Fields are Empty",Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun showReportForm() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.activity_report_form, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()


        // Find editText inside the activity_complain_form layout
        val oneStarExlpaination: EditText = view.findViewById(R.id.editTextTextMultiLine2)
        val acceptTerms: CheckBox = view.findViewById(R.id.checkBoxTerms)
        val btSubmitReport: AppCompatButton = view.findViewById(R.id.btSubmitReport)


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
        btSubmitReport.setOnClickListener {
            if (acceptTerms.isChecked) {
                bottomSheetDialog.dismiss()
                Toast.makeText(this,"Review and Report Submitted",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,"Please Accept Terms and Conditions",Toast.LENGTH_SHORT).show()
            }
        }



    }
    private fun showRate() {
        isRateButtonClickable = true
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.review_form, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()


        // Find views inside the review_form layout
        val ratingBar: RatingBar = view.findViewById(R.id.rbRate)
        val tvCurRating: TextView = view.findViewById(R.id.tvNumberofStars)
        val btRate: AppCompatButton = view.findViewById(R.id.acbRate)

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
                val textSizeMultiplier = 5f // Adjust this value to control the scaling effect
                val textSize = 10 +rating * textSizeMultiplier

                // Update the text and set the text size
                tvCurRating.text = String.format("%s / 5", rating.toInt())
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

            if (ratingBar.rating > 1) {
                bottomSheetDialog.dismiss()
                Toast.makeText(this, "Thanks for rating", Toast.LENGTH_SHORT).show();
            }
            else if (ratingBar.rating == 1.0f)
            {
                bottomSheetDialog.dismiss()
                showReportForm()
            }
        }
    }
    private fun showComment() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.activity_comment, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()

        val btPublish: Button = view.findViewById(R.id.btPublish)
        // Set the layout parameters
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        bottomSheetDialog.window?.setLayout(layoutParams.width, layoutParams.height)
        bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        bottomSheetDialog.window?.setWindowAnimations(R.style.DialogAnimation)
        bottomSheetDialog.window?.setGravity(Gravity.BOTTOM)

        btPublish.setOnClickListener {

            bottomSheetDialog.dismiss()
            Toast.makeText(this, "We apologise Comments are not enabled", Toast.LENGTH_SHORT).show();

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
            Complain("andreas", "1234",R.drawable.photo_rubish,3.5f),
            Complain("ilias", "567", R.drawable.photo_accident,4.0f),
            Complain("Title", "876",R.drawable.photo_flood,2.0f)
            // Add more complains as needed
        )
    }


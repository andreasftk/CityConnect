package cityconnnect.app
import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RatingBar
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
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
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import cityconnect.app.data.MyAPI
import cityconnnect.app.data.Complain
import cityconnnect.app.data.Complain.Companion.insertComplain
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import java.util.Locale
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class ComplainMain : AppCompatActivity(), ComplainAdapter.ImageButtonClickListener  {
    private lateinit var buttonFeed: Button
    private lateinit var buttonHistory: Button
    private lateinit var buttonPlus: ImageButton
    private lateinit var complainAdapter: ComplainAdapter
    private lateinit var rvComplains: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions: Array<String>
    private var imageUri: Uri? = null
    private lateinit var ivPhoto: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_complain_main) // Set correct layout resource

        rvComplains = findViewById(R.id.rvComplains)
        rvComplains.setHasFixedSize(true) // Optional: Improve performance if size won't change
        //complainAdapter = ComplainAdapter(getSampleComplains()) // Pass your list of complains here
        //complainAdapter = ComplainAdapter(mutableListOf())
        // Fetch complains data using Retrofit

        //val resourceId = R.drawable.photo_flood
        //Log.e("photos", "Resource ID: $resourceId")

        refreshComplains()
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        // Initialize the SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            refreshComplains()
        }

       // rvComplains.adapter = complainAdapter
        //rvComplains.layoutManager = LinearLayoutManager(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        cameraPermissions = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)


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



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

    }

    private fun refreshComplains() {
        Complain.getComplains(this) { complains ->
            val complainList = ArrayList(complains)
            complainAdapter = ComplainAdapter(complainList)
            rvComplains.adapter = complainAdapter
            rvComplains.layoutManager = LinearLayoutManager(this)
            complainAdapter.setImageButtonClickListener(this)
            swipeRefreshLayout.isRefreshing = false // Stop the refreshing animation

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
        var formCompleted = false
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.activity_complain_form, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()


        // Find editText inside the activity_complain_form layout
        val etLocation: EditText = view.findViewById(R.id.etLocation)
        val etFormTitle: EditText = view.findViewById(R.id.etFormTitle)
        val etDescription: EditText = view.findViewById(R.id.etDescription)
        val etSuggestions: EditText = view.findViewById(R.id.etSuggestions)
        val ibCamera: ImageButton = view.findViewById(R.id.ibCamera)
        val ibLocation: ImageButton = view.findViewById(R.id.ibLocation)
        val btSubmit: AppCompatButton = view.findViewById(R.id.btSubmit)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchCurrentLocation(etLocation)
        ivPhoto = view.findViewById(R.id.ivPhoto)

        fun isFormCompleted(): Boolean {
            return etLocation.text.isNotEmpty() &&
                    etFormTitle.text.isNotEmpty() &&
                    etDescription.text.isNotEmpty()
        }

        ibCamera.setOnClickListener {
            if (checkCameraPermissions()) {
                pickImageCamera()
            } else {
                requestCameraPermission()
            }

        }

    ibLocation.setOnClickListener {
            // Handle click on location button (ibLocation)
            // Fetch current location here
            fetchCurrentLocation(etLocation)
        }

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
            formCompleted = isFormCompleted()

            if (formCompleted) {
                insertComplain(
                    null,
                    etFormTitle.text.toString(),
                    etDescription.text.toString(),
                    etSuggestions.text.toString(),
                    R.drawable.logo_login, // Replace with the actual resource ID
                    0.0f,
                    null,
                    etLocation.text.toString(),
                    ) { isSuccess ->
                    if (isSuccess) {
                        // Complain successfully inserted
                        Toast.makeText(
                            this@ComplainMain,
                            "Data successfully inserted",
                            Toast.LENGTH_SHORT
                        ).show()
                        bottomSheetDialog.dismiss()
                        refreshComplains()
                    } else {
                        // Failed to insert complain
                        Toast.makeText(
                            this@ComplainMain,
                            "Failed to insert data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this@ComplainMain, "Required Fields are Empty", Toast.LENGTH_SHORT).show()
            }
        }


    }



    // This method will help to retrieve the image
    val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    private fun pickImageCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Sample Image")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }
    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            Log.d(TAG, "cameraActivityResultLauncher imageUri: $imageUri")
            ivPhoto.setImageURI(imageUri)
        }
    }
    private fun checkCameraPermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED)
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    pickImageCamera()
                } else {
                    showToast("Camera and Storage permissions are required")
                }
            }
            STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageCamera()
                } else {
                    showToast("Storage permission is required...")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }




    private fun fetchCurrentLocation(etLocation: EditText) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task: Task<android.location.Location> ->
            if (task.isSuccessful && task.result != null) {
                val currentLocation = task.result
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)

                if (addresses != null) {
                        val address = addresses[0]
                        val addressLine = address.getAddressLine(0) // You can modify this index to get more detailed address lines if needed
                        etLocation.setText(addressLine)

                }
                else {
                    // Handle the case when the location couldn't be retrieved
                    Toast.makeText(this, "Failed to fetch current address", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Handle the case when the location couldn't be retrieved
                Toast.makeText(this, "Location unavailable", Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST_CODE = 101
        private const val TAG = "MAIN_TAG"
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
/*
    private fun getSampleComplains(): MutableList<Complain> {
        // Sample data for testing
        return mutableListOf(
            Complain(1,"andreas", "1234","",R.drawable.photo_rubish,3.5f,,"Patra"),
            Complain(2,"ilias", "567","", R.drawable.photo_accident,4.0f),
            Complain(3,"Title", "876","",R.drawable.photo_flood,2.0f)
            // Add more complains as needed
        )
    }
*/

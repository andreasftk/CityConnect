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
import cityconnnect.app.data.Complain
import cityconnnect.app.data.Complain.Companion.insertComplain
import cityconnnect.app.data.Complain.Companion.updateComplain
import cityconnnect.app.Review.Companion.insertReview
import cityconnnect.app.Review.Companion.getReview
import cityconnnect.app.Report.Companion.insertReport
import cityconnnect.app.Report.Companion.getReport

import java.util.Locale
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class ComplainManageMain : AppCompatActivity(), ComplainManageAdapter.ImageButtonClickListener  {
    private lateinit var buttonFeed: Button
    private lateinit var buttonHistory: Button
    private lateinit var complainManageAdapter: ComplainManageAdapter
    private lateinit var rvComplains: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions: Array<String>
    private var imageUri: Uri? = null
    private lateinit var ivPhoto: ImageView

    private var currentUser: Int = 1
    private lateinit var complainList: ArrayList<Complain>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Retrieve the Bundle from the Intent
        val bundle = intent.extras

        // Extract the data from the Bundle
        if (bundle != null) {
            currentUser = bundle.getInt("id")
        }
        setContentView(R.layout.activity_manage_complain_main) // Set correct layout resource

        rvComplains = findViewById(R.id.rvComplains)
        rvComplains.setHasFixedSize(true) // Optional: Improve performance if size won't change
        //complainAdapter = ComplainAdapter(getSampleComplains()) // Pass your list of complains here
        //complainAdapter = ComplainAdapter(mutableListOf())
        // Fetch complains data using Retrofit


        //val resourceId1 = R.drawable.photo_disaster8

        //Log.e("photos", "Resource ID: $resourceId1")

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





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

    }

    private fun refreshComplains() {

        Complain.getComplains(this) { complains ->
            complainList = ArrayList(complains)
            complainManageAdapter = ComplainManageAdapter(complainList)
            rvComplains.adapter = complainManageAdapter
            rvComplains.layoutManager = LinearLayoutManager(this)
            complainManageAdapter.setImageButtonClickListener(this)
            swipeRefreshLayout.isRefreshing = false // Stop the refreshing animation

        }
    }

    private fun findReview(userId: Int, complainId: Int, ratingBar: RatingBar) {

        getReview(this) { reviews ->
            val matchingReview = reviews.find { it.userId == userId && it.complainId == complainId }
            val star = matchingReview?.star
            if (star != null) {
                ratingBar.rating = star.toFloat()
            } else {
                ratingBar.rating = 0f
            }
        }
    }

    private fun findReport(userId: Int, complainId: Int, oneStarExplanation: EditText) {
        getReport(this) { reports ->
            val matchingReport = reports.find { it.userId == userId && it.complainId == complainId }
            val text = matchingReport?.text
            oneStarExplanation.hint = text ?: "Type here"
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
                complainManageAdapter.filter.filter(newText)
                return true
            }
        })
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private var isRateButtonClickable = true

    override fun onImageButtonClick(complain: Complain, buttonType: ComplainManageAdapter.ButtonType) {
        searchView.clearFocus()
        if (isRateButtonClickable) {
            // Check which button is clicked and show the respective bottom sheet
            when (buttonType) {
                ComplainManageAdapter.ButtonType.SOLVED -> showSolvedForm(complain)
                ComplainManageAdapter.ButtonType.COMMENT -> showComment()
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

    private fun isSolved(text: String): Boolean {
        // Define the keywords that indicate the issue is solved
        val keyWords = arrayOf(
            "solved", "resolved", "fixed", "addressed",
            "handled", "authorities action", "closed",
            "completed", "settled", "taken care of",
            "sorted", "rectified", "attended to"
        )

        // Check if any of the keywords are present in the text
        for (keyword in keyWords) {
            if (text.contains(keyword, ignoreCase = true)) {
                Log.d(TAG, "Found a keyword indicating the issue is solved: $keyword")
                return true
            }
        }
        Log.d(TAG, "No keywords indicating the issue is solved found")
        return false
    }

    private fun areSimilar(text1: String, text2: String): Boolean {
        // You can implement a more complex similarity check here
        return text1.equals(text2, ignoreCase = true)
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



    private fun showSolvedForm(complain: Complain) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.activity_solved_form, null)
        val solvedExplanation: EditText = view.findViewById(R.id.editTextTextMultiLine3)
        val ibCamera: ImageButton = view.findViewById(R.id.ibCamera1)
        ivPhoto = view.findViewById(R.id.ivPhoto1)

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()


        // Find editText inside the activity_complain_form layout
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

        ibCamera.setOnClickListener {
            if (checkCameraPermissions()) {
                pickImageCamera()
            } else {
                requestCameraPermission()
            }

        }
        // Set listener on RatingBar to update the TextView
        btSubmitReport.setOnClickListener {
            if (acceptTerms.isChecked) {
                if(ivPhoto.drawable != null)   {
                if(isSolved(solvedExplanation.text.toString()))    {
                        updateComplain(
                            complain.complainId,
                            "Solved: ${complain.title}"
                        ) { isSuccess ->
                            if (isSuccess) {
                                // Complain successfully inserted
                                Toast.makeText(
                                    this@ComplainManageMain,
                                    "Complain solved successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                bottomSheetDialog.dismiss()
                                refreshComplains()
                            } else {
                                // Failed to insert complain
                                Toast.makeText(
                                    this@ComplainManageMain,
                                    "Failed to insert data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    else{
                    Toast.makeText(
                        this@ComplainManageMain,
                        "Your explanation is not enough",
                        Toast.LENGTH_SHORT
                    ).show()
                    }
                }
                else
                {
                    Toast.makeText(
                        this@ComplainManageMain,
                        "Please take a photo",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Toast.makeText(this,"Please Accept Terms and Conditions",Toast.LENGTH_SHORT).show()
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

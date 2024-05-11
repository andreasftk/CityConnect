package cityconnnect.app

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class qrscanner : AppCompatActivity() {
    //UI Views
    private lateinit var cameraBtn: MaterialButton
    private lateinit var galleryBtn: MaterialButton
    private lateinit var imageIv: ImageView
    private lateinit var scanBtn: MaterialButton
    private lateinit var resultTv: TextView

    companion object {
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST_CODE = 101
        private const val TAG = "MAIN_TAG"
    }

    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions: Array<String>
    private var imageUri: Uri? = null
    private var barcodeScannerOptions: BarcodeScannerOptions? = null
    private var barcodeScanner: BarcodeScanner? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner)

        //init UI Views
        cameraBtn = findViewById(R.id.cameraBtn)
        galleryBtn = findViewById(R.id.galleryBtn)
        imageIv = findViewById(R.id.imageIv)
        scanBtn = findViewById(R.id.scanBtn)
        resultTv = findViewById(R.id.resultTv)

        cameraPermissions = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        barcodeScannerOptions = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
        barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions!!)

        cameraBtn.setOnClickListener {
            if (checkCameraPermissions()) {
                pickImageCamera()
            } else {
                requestCameraPermission()
            }
        }

        galleryBtn.setOnClickListener {
            if (checkStoragePermissions()) {
                pickImageGallery()
            } else {
                requestStoragePermission()
            }
        }

        scanBtn.setOnClickListener {
            if (imageUri == null) {
                showToast("Pick Image First")
            } else {
                detectResultFromImage()
            }
        }
    }

    private fun detectResultFromImage() {
        Log.d(TAG, "detectResultFromImage:")
        try {
            val inputImage = InputImage.fromFilePath(this, imageUri!!)
            barcodeScanner!!.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    extractBarcodeQrCodeInfo(barcodes)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "detectResultFromImage: ", e)
                    showToast("Failed scanning due to ${e.message}")
                }
        } catch (e: Exception) {
            Log.e(TAG, "detectResultFromImage: ", e)
            showToast("Failed due to ${e.message}")
        }
    }

    private fun extractBarcodeQrCodeInfo(barcodes: List<Barcode>) {
        for (barcode in barcodes) {
            val rawValue = barcode.rawValue ?: ""
            val valueType = barcode.valueType

            when (valueType) {
                Barcode.TYPE_WIFI -> {
                    val typeWifi = barcode.wifi
                    val ssid = typeWifi?.ssid ?: ""
                    val password = typeWifi?.password ?: ""
                    var encryptionType = typeWifi?.encryptionType ?: ""
                    if (encryptionType == "1") {
                        encryptionType = "OPEN"
                    } else if (encryptionType == "2") {
                        encryptionType = "WPA"
                    } else if (encryptionType == "3") {
                        encryptionType = "WEP"
                    }
                    resultTv.text = "TYPE_WIFI \nssid: $ssid \npassword: $password \nencryptionType: $encryptionType \n\nrawValue: $rawValue"
                }
                Barcode.TYPE_URL -> {
                    val title = barcode.url?.title ?: ""
                    resultTv.text = "TYPE_URL \ntitle: $title : $rawValue"
                }
                Barcode.TYPE_EMAIL -> {
                    val address = barcode.email?.address ?: ""
                    val body = barcode.email?.body ?: ""
                    val subject = barcode.email?.subject ?: ""
                    resultTv.text = "TYPE_EMAIL \nEmail: $address \nbody:  $body \nsubject: $subject \n\nrawValue: $rawValue"
                }
                Barcode.TYPE_CONTACT_INFO -> {
                    val name = "${barcode.contactInfo?.name?.first} ${barcode.contactInfo?.name?.last}" ?: ""
                    val phone = barcode.contactInfo?.phones?.getOrNull(0)?.number ?: ""
                    resultTv.text = "TYPE_CONTACT_INFO \nname: $name \nphone: $phone\n\nrawValue: $rawValue"
                }
                else -> {
                    resultTv.text = "rawValue: $rawValue"
                }
            }
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            imageUri = data?.data
            Log.d(TAG, "galleryActivityResultLauncher: imageUri: $imageUri")
            imageIv.setImageURI(imageUri)
        } else {
            showToast("Cancelled!!!!")
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
            imageIv.setImageURI(imageUri)
        }
    }

    private fun checkStoragePermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE)
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
}

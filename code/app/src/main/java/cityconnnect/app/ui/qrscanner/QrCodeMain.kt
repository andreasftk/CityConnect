package cityconnnect.app.ui.qrscanner.zxinglibrary
import cityconnnect.app.ui.qrscanner.zxinglibrary.IntegerRequest


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import cityconnnect.app.R
import com.google.zxing.integration.android.IntentIntegrator

import cityconnnect.app.databinding.QrCodeMainBinding
import org.json.JSONException
import org.json.JSONObject

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request.Method

import com.android.volley.NetworkResponse

import com.android.volley.toolbox.HttpHeaderParser
import java.io.UnsupportedEncodingException



class QrCodeMain: AppCompatActivity() {

    private lateinit var binding: QrCodeMainBinding
    private var qrScanIntegrator: IntentIntegrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = QrCodeMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setOnClickListener()
        setupScanner()
    }
    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator(this)
    }

    private fun setOnClickListener() {
        binding.btnScan.setOnClickListener { performAction() }

        binding.showQRScanner.setOnClickListener {
            // Add code to show QR Scanner Code in Fragment.
        }
    }


    private fun performAction() {
        qrScanIntegrator?.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, getString(R.string.result_not_found), Toast.LENGTH_LONG).show()
            } else {
                // If QRCode contains data, send it to the PHP script for insertion
                sendDataToServer(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun sendDataToServer(qrData: String) {
        val requestQueue = Volley.newRequestQueue(this)
        val selectUrl = "https://example.com/select_data.php"
        val updateUrl = "https://example.com/update_data.php"

        val selectRequest = IntegerRequest(
            Method.POST, selectUrl,
            { result ->
                if (result == 0) {
                    // Data does not exist, perform insertion
                    performInsertion(qrData, updateUrl, requestQueue)
                } else {
                    // Data exists, perform update
                    performUpdate(qrData, updateUrl, requestQueue)
                }
            },
            { error ->
                // Handle errors
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        // Add select request to the queue
        requestQueue.add(selectRequest)
    }


    private fun performInsertion(qrData: String, updateUrl: String, requestQueue: RequestQueue) {
        val insertUrl = "https://example.com/insert_data.php"
        // Perform insertion query
        val stringRequest = object : StringRequest(
            Request.Method.POST, insertUrl,
            Response.Listener<String> { response ->
                // Handle the response from the server
                Toast.makeText(this, response, Toast.LENGTH_LONG).show()
            },
            Response.ErrorListener { error ->
                // Handle errors
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                // Add parameters to your request (e.g., the QR data)
                val params = HashMap<String, String>()
                params["qr_data"] = qrData
                return params
            }
        }
        requestQueue.add(stringRequest)
    }


    private fun performUpdate(qrData: String, updateUrl: String, requestQueue: RequestQueue) {
        // Perform update query
        val stringRequest = object : StringRequest(
            Request.Method.POST, updateUrl,
            Response.Listener<String> { response ->
                // Handle the response from the server
                Toast.makeText(this, response, Toast.LENGTH_LONG).show()
            },
            Response.ErrorListener { error ->
                // Handle errors
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                // Add parameters to your request (e.g., the QR data)
                val params = HashMap<String, String>()
                params["qr_data"] = qrData
                return params
            }
        }
        requestQueue.add(stringRequest)
    }




}

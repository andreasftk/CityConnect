package cityconnnect.app.ui.bills

import PendingBillsAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cityconnnect.app.R
import cityconnnect.app.data.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class PendingBillsFragment : Fragment(), PendingBillsAdapter.OnCheckedChangeListener {

    private var pendingBills: MutableList<PendingBill> = mutableListOf()
    private var totalAmount: Double = 0.0
    private lateinit var totalAmountTextView: TextView
    private var userId: Int = -1 // Class-level variable to store user ID

    companion object {
        private const val ARG_USER_ID = "user_id"

        fun newInstance(userId: Int): PendingBillsFragment {
            val fragment = PendingBillsFragment()
            val args = Bundle()
            args.putInt(ARG_USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getInt(ARG_USER_ID) ?: -1 // Retrieve the user ID from arguments
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pending_bills, container, false)

        // Handle pay button click
        val payButton = view.findViewById<Button>(R.id.buttonPay)
        payButton.setOnClickListener {
            paySelectedBills()
        }

        totalAmountTextView = view.findViewById(R.id.totalAmountTextView)

        lifecycleScope.launch {
            loadPendingBills(userId) // Pass the userId to the method
        }

        return view
    }

    override fun onCheckedChanged(amount: Double, isChecked: Boolean) {
        totalAmount += if (isChecked) amount else -amount
        totalAmountTextView.text = "Total Amount: ${String.format("%.2f", totalAmount)}€"
    }

    private suspend fun loadPendingBills(citizenId: Int) {
        try {
            Log.d("API_CALL", "Fetching pending bills for user ID: $citizenId")
            val bills = ApiClient.apiService.getPendingBills(citizenId)
            Log.d("API_CALL", "Received response: $bills")
            pendingBills = bills.toMutableList()

            // Add a log statement to check the contents of pendingBills
            Log.d("PendingBills", "Number of pending bills: ${pendingBills.size}")

            // Update RecyclerView with pending bills
            val recyclerView = requireView().findViewById<RecyclerView>(R.id.pendingBillRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = PendingBillsAdapter(pendingBills, this)

            // Update UI with pending bills
            // You can perform any additional UI updates here
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun paySelectedBills() {
        val selectedBills = (requireView().findViewById<RecyclerView>(R.id.pendingBillRecyclerView).adapter as PendingBillsAdapter).getSelectedBills()

        // Extract bill IDs from selected bills
        val selectedBillIds = selectedBills.map { it.billId }

        // Log the JSON format
        Log.d("JSON_FORMAT", selectedBillIds.toString())

        // Inside the paySelectedBills() function
        val jsonObject = JSONObject().apply {
            put("selectedBillIds", JSONArray(selectedBillIds))
        }

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaType())

        lifecycleScope.launch {
            try {
                Log.d("PAY_BILLS_REQUEST", "Selected Bill IDs: $selectedBillIds")
                Log.d("JSON_REQUEST", jsonObject.toString())
                val response = ApiClient.apiService.payBills(requestBody)
                Log.d("PAY_BILLS_RESPONSE", "Response: ${response.string()}")

                // Reset total amount to 0.0€ after paying bills
                totalAmount = 0.0
                totalAmountTextView.text = "Total Amount: ${String.format("%.2f", totalAmount)}€"

                // Refresh the pending bills list after paying bills
                loadPendingBills(userId) // Use the userId to refresh the list

            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
            }
        }
    }
}

package cityconnnect.app.ui.bills

import PendingBillsAdapter
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cityconnnect.app.R
import cityconnnect.app.data.ApiClient
import cityconnnect.app.data.Bill
import kotlinx.coroutines.launch

class BillSelectionActivity : AppCompatActivity(), PendingBillsAdapter.OnCheckedChangeListener {

    private var pendingBills: MutableList<Bill> = mutableListOf()
    private var totalAmount: Double = 0.0
    private lateinit var totalAmountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_selection)

        totalAmountTextView = findViewById(R.id.totalAmountTextView)

        lifecycleScope.launch {
            loadPendingBills(1)
        }
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
            val recyclerView = findViewById<RecyclerView>(R.id.pendingBillRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = PendingBillsAdapter(pendingBills, this)

            // Update UI with pending bills
            // You can perform any additional UI updates here
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun payBill(billId: Int) {
        try {
            val response = ApiClient.apiService.payBill(billId)
            if (response.string() == "Success") {
                pendingBills.removeAll { it.billId == billId }
                // Update UI to reflect changes
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

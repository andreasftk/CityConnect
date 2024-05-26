package cityconnnect.app.ui.bills

import PaidBillsAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cityconnnect.app.R
import cityconnnect.app.data.ApiClient
import cityconnnect.app.data.PaidBill
import kotlinx.coroutines.launch

class PaidBillsFragment : Fragment() {

    private var paidBills: MutableList<PaidBill> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_paid_bills, container, false)

        lifecycleScope.launch {
            loadPaidBills(1) // Pass citizenId if needed
        }

        return view
    }

    private suspend fun loadPaidBills(citizenId: Int) {
        try {
            Log.d("API_CALL", "Fetching paid bills for user ID: $citizenId")
            val bills = ApiClient.apiService.getPaidBills(citizenId)
            Log.d("API_CALL", "Received response: $bills")
            paidBills = bills.toMutableList()

            // Add a log statement to check the contents of paidBills
            Log.d("PaidBills", "Number of paid bills: ${paidBills.size}")

            // Update RecyclerView with paid bills
            val recyclerView = requireView().findViewById<RecyclerView>(R.id.paidBillRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = PaidBillsAdapter(paidBills)

            // Update UI with paid bills
            // You can perform any additional UI updates here
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

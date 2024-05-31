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
import kotlinx.coroutines.launch

class PaidBillsFragment : Fragment() {

    private var paidBills: MutableList<PaidBill> = mutableListOf()
    private var userId: Int = -1 // Class-level variable to store user ID

    companion object {
        private const val ARG_USER_ID = "user_id"

        fun newInstance(userId: Int): PaidBillsFragment {
            val fragment = PaidBillsFragment()
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
        val view = inflater.inflate(R.layout.fragment_paid_bills, container, false)

        lifecycleScope.launch {
            loadPaidBills(userId) // Pass the userId to the method
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

package cityconnnect.app.ui.bills

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cityconnnect.app.R
import cityconnnect.app.data.PendingBills

class BillSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_selection)

        val bills = listOf(
            PendingBills("Electricity", 51.0, "2018-12-12", 1,1),
            PendingBills("Internet", 30.0, "2019-5-6", 2, 2),
            PendingBills("Water", 20.0, "2021-8-3", 3, 3)
        )
        // Assuming you have a RecyclerView with id 'billRecyclerView' in your layout
        val recyclerView: RecyclerView = findViewById(R.id.billRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = BillAdapter(bills)
        recyclerView.adapter = adapter

    }
}

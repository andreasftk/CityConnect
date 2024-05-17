package cityconnnect.app.ui.bills

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cityconnnect.app.R
import cityconnnect.app.data.Bill

class BillSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_selection)

        val bills = listOf(
            Bill(1, "Electricity", 50.0),
            Bill(2, "Internet", 30.0),
            Bill(3, "Water", 20.0)
        )
        // Assuming you have a RecyclerView with id 'billRecyclerView' in your layout
        val recyclerView: RecyclerView = findViewById(R.id.billRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = BillAdapter(bills)
        recyclerView.adapter = adapter

    }
}

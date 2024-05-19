package cityconnnect.app.ui.bills

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cityconnnect.app.R
import cityconnnect.app.data.PendingBills

class BillAdapter(private val bills: List<PendingBills>) : RecyclerView.Adapter<BillAdapter.BillViewHolder>() {

    // ViewHolder class to hold references to the views
    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val billNameTextView: TextView = itemView.findViewById(R.id.billNameTextView)
        val billDateTextView: TextView = itemView.findViewById(R.id.billDateTextView)
        val billAmountTextView: TextView = itemView.findViewById(R.id.billAmountTextView)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        // Create a new view
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_bill, parent, false)
        return BillViewHolder(itemView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        // Get the bill at the specified position
        val bill = bills[position]

        // Set the text for the TextViews in the ViewHolder
        holder.billNameTextView.text = bill.title
        holder.billDateTextView.text = bill.date
        holder.billAmountTextView.text = "${bill.amount} €" // Assuming you want to display the amount with a currency symbol
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return bills.size
    }
}

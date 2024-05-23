import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cityconnnect.app.R
import cityconnnect.app.data.Bill

class PendingBillsAdapter(private val pendingBills: List<Bill>) :
    RecyclerView.Adapter<PendingBillsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.billNameTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.billAmountTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.billDateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_bill, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bill = pendingBills[position]
        holder.titleTextView.text = bill.title
        holder.amountTextView.text = "Amount: ${bill.amount}"
        holder.dateTextView.text = "Date: ${bill.date}"
    }

    override fun getItemCount(): Int {
        return pendingBills.size
    }
}

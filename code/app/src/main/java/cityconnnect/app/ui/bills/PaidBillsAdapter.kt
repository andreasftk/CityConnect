import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cityconnnect.app.R
import cityconnnect.app.data.PaidBill

class PaidBillsAdapter(
    private val paidBills: List<PaidBill>,
) : RecyclerView.Adapter<PaidBillsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.paidBillTitleTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.paidBillAmountTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.paidBillDateTextView)
        val receiptTextView: TextView = itemView.findViewById(R.id.paidBillReceiptTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_paid_bill, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bill = paidBills[position]
        holder.titleTextView.text = bill.title
        holder.amountTextView.text = "Amount: ${bill.amount}€"
        holder.dateTextView.text = "Date: ${bill.date}"
        holder.receiptTextView.text = bill.receipt
    }

    override fun getItemCount(): Int {
        return paidBills.size
    }
}

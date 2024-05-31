import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cityconnnect.app.R
import cityconnnect.app.ui.bills.PendingBill

class PendingBillsAdapter(
    private val pendingBills: List<PendingBill>,
    private val listener: OnCheckedChangeListener
) : RecyclerView.Adapter<PendingBillsAdapter.ViewHolder>() {

    // Add a property to store selected bills
    private val selectedBills: MutableList<PendingBill> = mutableListOf()

    interface OnCheckedChangeListener {
        fun onCheckedChanged(amount: Double, isChecked: Boolean)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: CheckBox = itemView.findViewById(R.id.pendingBillTitleTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.pendingBillAmountTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.pendingBillDateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_pending_bill, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bill = pendingBills[position]
        holder.titleTextView.text = bill.title
        holder.amountTextView.text = "Amount: ${bill.amount}€"
        holder.dateTextView.text = "Date: ${bill.date}"

        // Update selectedBills list based on checkbox state
        holder.titleTextView.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedBills.add(bill)
            } else {
                selectedBills.remove(bill)
            }
            listener.onCheckedChanged(bill.amount, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return pendingBills.size
    }

    // Add a method to get selected bills
    fun getSelectedBills(): List<PendingBill> {
        return selectedBills
    }
}

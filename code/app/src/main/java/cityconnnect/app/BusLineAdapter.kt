// BusLineAdapter.kt
package cityconnnect.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BusLineAdapter(
    private val lines: MutableList<BusLine>
) : RecyclerView.Adapter<BusLineAdapter.BusLineViewHolder>() {

    private var filteredList: MutableList<BusLine> = lines.toMutableList()
    private var itemClickListener: ItemClickListener? = null

    interface ItemClickListener {
        fun onItemClick(line: BusLine)
    }

    fun setItemClickListener(listener: ItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusLineViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_bus_line, parent, false)
        return BusLineViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BusLineViewHolder, position: Int) {
        val currentBusLine = filteredList[position]
        holder.bind(currentBusLine)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    inner class BusLineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvBusLineId: TextView = itemView.findViewById(R.id.tvBusLineId)

        fun bind(busLine: BusLine) {
            tvBusLineId.text = "Line ${busLine.id}"
            itemView.setOnClickListener {
                itemClickListener?.onItemClick(busLine)
            }
        }
    }
}

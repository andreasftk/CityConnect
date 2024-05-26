// ParkingAdapter.kt
package cityconnnect.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ParkingAdapter(
    private val parkings: MutableList<Parkings>
) : RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder>() {

    private var filteredList: MutableList<Parkings> = parkings.toMutableList()
    private var itemClickListener: ItemClickListener? = null

    interface ItemClickListener {
        fun onItemClick(parking: Parkings)
        fun onGoToPageClick(parking: Parkings)
    }

    fun setItemClickListener(listener: ItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_parking_name, parent, false)
        return ParkingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ParkingViewHolder, position: Int) {
        val currentParking = filteredList[position]
        holder.bind(currentParking)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    inner class ParkingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvParkingAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val buttonGoToPage: Button = itemView.findViewById(R.id.go_to_page_list)

        fun bind(parking: Parkings) {
            tvParkingAddress.text = parking.address
            itemView.setOnClickListener {
                itemClickListener?.onItemClick(parking)
            }
            buttonGoToPage.setOnClickListener {
                itemClickListener?.onGoToPageClick(parking)
            }
        }
    }
}

